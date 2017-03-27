package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.okta.OktaTokenResponse;
import com.stormpath.sdk.application.okta.OktaTokenRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.impl.application.okta.DefaultOktaSigningKeyResolver;
import com.stormpath.sdk.impl.application.okta.OktaSigningKeyResolver;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.resource.ResourceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Uses Okta's /api/v1/authn endpoint to authenticate users.
 */
public class OktaAuthNAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(OktaAuthNAuthenticator.class);

    private final InternalDataStore dataStore;

    public OktaAuthNAuthenticator(InternalDataStore dataStore) {
        Assert.notNull(dataStore);
        Assert.notNull(dataStore);
        this.dataStore = dataStore;
    }


    public AuthenticationResult authenticate(DefaultUsernamePasswordRequest request) {

        final OktaTokenResponse oktaTokenResponse = doAuthRequest(request);

        // validate the key we just received
        SigningKeyResolver keyResolver = dataStore.instantiate(OktaSigningKeyResolver.class);
        final Jwt<Header, Claims> jwt = Jwts.parser()
                .setSigningKeyResolver(keyResolver)
                .parse(oktaTokenResponse.getAccessToken());
        String userId = jwt.getBody().get("uid", String.class);


        final String userHref = "/api/v1/users/" + userId;

        Map<String, Object> authMap = new LinkedHashMap<>();
        Map<String, Object> accountMap = new LinkedHashMap<>();
        authMap.put("account", accountMap);
        accountMap.put("href", userHref);

        final Set<String> scopes = new HashSet<>(jwt.getBody().get("scp", ArrayList.class));

        return new AccessTokenResult() {
            @Override
            public com.stormpath.sdk.oauth.TokenResponse getTokenResponse() {
                return oktaTokenResponse;
            }

            @Override
            public Set<String> getScope() {
                return scopes;
            }

            @Override
            public ApiKey getApiKey() {
                return null;
            }

            @Override
            public Account getAccount() {
                return dataStore.getResource(userHref, Account.class);
            }

            @Override
            public void accept(AuthenticationResultVisitor visitor) {
                visitor.visit(this);
            }

            @Override
            public String getHref() {
                return null;
            }
        };
    }

    private OktaTokenResponse doAuthRequest(DefaultUsernamePasswordRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // hit the oauth token endpoint
        OktaTokenRequest tokenRequest = this.dataStore.instantiate(OktaTokenRequest.class)
                .setPassword(new String(request.getCredentials()))
                .setUsername(request.getPrincipals())
                .setGrantType("password")
                .setScope("offline_access");

        try {

            return this.dataStore.create("/oauth2/v1/token", tokenRequest, OktaTokenResponse.class, httpHeaders);
        }
        catch (final ResourceException e) {

            throw  new ResourceException(new Error() {
                @Override
                public int getStatus() {
                    return e.getStatus();
                }

                @Override
                public int getCode() {
                    return 0;
                }

                @Override
                public String getMessage() {
                    // TODO: i18n, configure error handler for this type of message?
                    return "Invalid username or password.";
                }

                @Override
                public String getDeveloperMessage() {
                    return e.getDeveloperMessage();
                }

                @Override
                public String getMoreInfo() {
                    return e.getMoreInfo();
                }

                @Override
                public String getRequestId() {
                    return e.getRequestId();
                }
            });
        }
    }

}
