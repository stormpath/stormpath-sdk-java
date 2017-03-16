package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.okta.OIDCKey;
import com.stormpath.sdk.application.okta.OIDCKeysList;
import com.stormpath.sdk.application.okta.OktaTokenResponse;
import com.stormpath.sdk.application.okta.TokenIntrospectRequest;
import com.stormpath.sdk.application.okta.TokenIntrospectResponse;
import com.stormpath.sdk.application.okta.OktaTokenRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.application.okta.OktaSigningKeyResolver;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessTokenResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
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

        // hit the oauth token endpoint
        OktaTokenRequest tokenRequest = this.dataStore.instantiate(OktaTokenRequest.class);
        tokenRequest.setPassword(new String(request.getCredentials()));
        tokenRequest.setUsername(request.getPrincipals());
        tokenRequest.setGrantType("password");
        tokenRequest.setScope("offline_access");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final OktaTokenResponse oktaTokenResponse = this.dataStore.create("/oauth2/v1/token", tokenRequest, OktaTokenResponse.class, httpHeaders);


        // introspect it with access token
        TokenIntrospectRequest tokenIntrospectRequest = this.dataStore.instantiate(TokenIntrospectRequest.class);
        tokenIntrospectRequest.setToken(oktaTokenResponse.getAccessToken());
        tokenIntrospectRequest.setTokenTypeHint("access_token");

//        TokenIntrospectResponse tokenIntrospectResponse = this.dataStore.create("/oauth2/v1/introspect", tokenIntrospectRequest, TokenIntrospectResponse.class, httpHeaders);

        // possibly use JWT, but need to configure keys

        SigningKeyResolver keyResolver = new OktaSigningKeyResolver(dataStore);
        final Jwt<Header, Claims> jwt = Jwts.parser()
                .setSigningKeyResolver(keyResolver)
                .parse(oktaTokenResponse.getAccessToken());
        String userId = (String) jwt.getBody().get("uid"); // FIXME: ugly cast


        final String userHref = "/api/v1/users/" + userId;

        Map<String, Object> authMap = new LinkedHashMap<>();
        Map<String, Object> accountMap = new LinkedHashMap<>();
        authMap.put("account", accountMap);
        accountMap.put("href", userHref);

        final Set<String> scopes = new HashSet<>(jwt.getBody().get("scp", ArrayList.class));

        AccessTokenResult result = new AccessTokenResult() {
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
                return ApiKeys.builder()
                        .setId("foobar")
                        .setSecret("barfoo")
                        .build();
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

        return result;

//        // return AccessTokenResult from all the above calls
//        return this.dataStore.instantiate(AuthenticationResult.class, authMap);


    }

}
