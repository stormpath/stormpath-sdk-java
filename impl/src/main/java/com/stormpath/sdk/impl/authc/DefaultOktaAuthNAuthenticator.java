package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.okta.OktaTokenResponse;
import com.stormpath.sdk.application.okta.OktaTokenRequest;
import com.stormpath.sdk.application.okta.TokenIntrospectRequest;
import com.stormpath.sdk.application.okta.TokenIntrospectResponse;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.authc.OktaAuthNAuthenticator;
import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.impl.provider.DefaultOktaProviderAccountResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.provider.OktaProviderData;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.resource.ResourceException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Uses Okta's /api/v1/authn endpoint to authenticate users.
 */
public class DefaultOktaAuthNAuthenticator implements OktaAuthNAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(DefaultOktaAuthNAuthenticator.class);

    private final InternalDataStore dataStore;

    public DefaultOktaAuthNAuthenticator(InternalDataStore dataStore) {
        Assert.notNull(dataStore);
        this.dataStore = dataStore;
    }

    @Override
    public ProviderAccountResult getAccount(ProviderAccountRequest request) {

        OktaProviderData oktaProviderData = (OktaProviderData) request.getProviderData();

        OktaTokenRequest tokenRequest = dataStore.instantiate(OktaTokenRequest.class);
        tokenRequest.setCode(oktaProviderData.getCode());
        tokenRequest.setGrantType("authorization_code");
        tokenRequest.setRedirectUri(request.getRedirectUri());

        OktaTokenResponse oktaTokenResponse = dataStore.create("/oauth2/v1/token", tokenRequest, OktaTokenResponse.class, getHeaders());

        // check if access key is valid
        TokenIntrospectResponse tokenIntrospectResponse = resolveAccessToken(oktaTokenResponse.getAccessToken());

        // validate the key we just received
        final String userHref = getAccountHref(tokenIntrospectResponse);

        return new DefaultOktaProviderAccountResult(dataStore.getResource(userHref, Account.class), oktaTokenResponse);
    }

    @Override
    public AuthenticationResult authenticate(AuthenticationRequest request) {

        Assert.isInstanceOf(DefaultUsernamePasswordRequest.class, request, "Only 'DefaultUsernamePasswordRequest' requests are supported.");
        DefaultUsernamePasswordRequest usernamePasswordRequest = (DefaultUsernamePasswordRequest) request;

        final OktaTokenResponse oktaTokenResponse = doAuthRequest(usernamePasswordRequest);

        // check if access key is valid
        TokenIntrospectResponse tokenIntrospectResponse = resolveAccessToken(oktaTokenResponse.getAccessToken());

        // validate the key we just received
        final String userHref = getAccountHref(tokenIntrospectResponse);

        final Set<String> scopes = Strings.delimitedListToSet(tokenIntrospectResponse.getScope(), "");

        // TODO: fix nested class
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

    private TokenIntrospectResponse resolveAccessToken(String accessToken) {

        TokenIntrospectRequest request = dataStore.instantiate(TokenIntrospectRequest.class)
            .setToken(accessToken)
            .setTokenTypeHint("access_token");

        TokenIntrospectResponse tokenIntrospectResponse = dataStore.create("/oauth2/v1/introspect", request, TokenIntrospectResponse.class, getHeaders());

        // fail if token is invalid
        assertValidAccessToken(tokenIntrospectResponse);

        return tokenIntrospectResponse;
    }

    public void assertValidAccessToken(TokenIntrospectResponse tokenResponse) {

        if(!tokenResponse.isActive()) {
            // FIXME: we should not use JWT exceptions here as this string should _not_ be treated as a JWT
            throw new JwtException("Access token is NOT active.");
        }

    }

    @Override
    public void assertValidAccessToken(String accessToken) {
        assertValidAccessToken(resolveAccessToken(accessToken));
    }


    private OktaTokenResponse doAuthRequest(DefaultUsernamePasswordRequest request) {

        // hit the oauth token endpoint
        OktaTokenRequest tokenRequest = this.dataStore.instantiate(OktaTokenRequest.class)
                .setPassword(new String(request.getCredentials()))
                .setUsername(request.getPrincipals())
                .setGrantType("password")
                .setScope("offline_access");

        try {

            return this.dataStore.create("/oauth2/v1/token", tokenRequest, OktaTokenResponse.class, getHeaders());
        }
        catch (final ResourceException e) {

            log.debug("Exception thrown while requesting token, assuming this is an Invalid username or password", e);

            // TODO: fix nested class
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

    private String getAccountHref(TokenIntrospectResponse tokenResponse) {
        return "/api/v1/users/" + tokenResponse.getUid();
    }

    @Override
    public String getHref() {
        return null;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return httpHeaders;
    }
}
