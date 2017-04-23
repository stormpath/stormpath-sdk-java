package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.error.DefaultError;
import com.stormpath.sdk.impl.okta.OktaApiPaths;
import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequestAuthentication;
import com.stormpath.sdk.resource.ResourceException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.text.MessageFormat;
import java.util.Date;

/**
 *
 */
public class OktaOAuthClientCredentialsGrantRequestAuthenticator extends DefaultOAuthClientCredentialsGrantRequestAuthenticator {

    private final Logger log = LoggerFactory.getLogger(OktaOAuthClientCredentialsGrantRequestAuthenticator.class);

    private final String userApiQueryTemplate;

    public OktaOAuthClientCredentialsGrantRequestAuthenticator(Application application, DataStore dataStore, String oauthTokenPath, String userApiQueryTemplate) {
        super(application, dataStore, oauthTokenPath);
        this.userApiQueryTemplate = userApiQueryTemplate;
    }

    @Override
    public OAuthGrantRequestAuthenticationResult authenticate(OAuthRequestAuthentication authenticationRequest) {
        Assert.notNull(this.getOauthTokenPath(), "oauthTokenPath cannot be null or empty");
        Assert.isInstanceOf(OAuthClientCredentialsGrantRequestAuthentication.class, authenticationRequest, "authenticationRequest must be an instance of OAuthClientCredentialsGrantRequestAuthentication.");
        OAuthClientCredentialsGrantRequestAuthentication oAuthClientCredentialsGrantRequestAuthentication = (OAuthClientCredentialsGrantRequestAuthentication) authenticationRequest;

        String id = oAuthClientCredentialsGrantRequestAuthentication.getApiKeyId();
        String secret = oAuthClientCredentialsGrantRequestAuthentication.getApiKeySecret();
        String grantType = oAuthClientCredentialsGrantRequestAuthentication.getGrantType();


        // we need to check if the id and secret are base64 encoded strings, if not this could be an injection attack
        Assert.isTrue(Base64.isBase64(id) && Base64.isBase64(secret), "Client credentials id:secret must be base64 encoded strings");

        String idSecret = id +":"+secret;
        String queryString = MessageFormat.format(userApiQueryTemplate, id, secret);
        String queryHref = OktaApiPaths.apiPath("users" + queryString);

        AccountList searchResults = dataStore.getResource(queryHref, AccountList.class);

        if (searchResults.getSize() > 1) {
            log.warn("Multiple users are using the same API key with id {}", id);
        }

        Account account = null;
        for (Account tmpAccount : searchResults) {

            CustomData customData = tmpAccount.getCustomData();
            for (int ii = 0; ii < 10; ii++) {
                if (idSecret.equals(customData.get("stormpathApiKey_" + ii))) {
                    account = tmpAccount;
                    break;
                }
            }
        }

        if (account == null) {
            throw resourceException();
        }

        Date expires = new DateTime().plusHours(1).toDate();
        String accountHref = account.getHref();
        String accountUid = accountHref.substring(accountHref.lastIndexOf('/')+1);
        String spGrantType = "sp_" + grantType;
        String tokenId = spGrantType +":"+ id;

        String accessTokenJwt = Jwts.builder()
                                    .signWith(SignatureAlgorithm.HS512, getSpSigningKey(SignatureAlgorithm.HS512))
                                    .setHeaderParam("grantType", spGrantType)
                                    .setId(tokenId)
                                    .setExpiration(expires)
                                    .claim("accountHref", accountHref)
                                    .claim("uid", accountUid)
                                    .compact();

        AccessToken token = new SimpleIntrospectAccessToken(accessTokenJwt, account, application);

        return new DefaultOAuthGrantRequestAuthenticationResult(token,
                                                                accessTokenJwt,
                                                                tokenId,
                                                                null,
                                                                null,
                                                                null,
                                                                spGrantType,
                                                                3600);
    }

    private ResourceException resourceException() {
        return new ResourceException(
                new DefaultError()
                        .setCode(400)
                        .setStatus(400)
                        .setMessage("Client id:secret combination was not valid."));
    }

    protected Key getSpSigningKey(SignatureAlgorithm alg) {
        String apiKeySecret = dataStore.getApiKey().getSecret();
        byte[] apiKeySecretBytes = Base64.decodeBase64(apiKeySecret);
        return new SecretKeySpec(apiKeySecretBytes, alg.getJcaName());
    }
}
