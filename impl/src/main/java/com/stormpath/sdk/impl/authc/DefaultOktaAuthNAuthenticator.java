package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.impl.oauth.DefaultOAuthRefreshTokenRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthRequestAuthentication;
import com.stormpath.sdk.okta.OktaTokenResponse;
import com.stormpath.sdk.okta.OktaTokenRequest;
import com.stormpath.sdk.okta.TokenIntrospectRequest;
import com.stormpath.sdk.okta.TokenIntrospectResponse;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.OktaAuthNAuthenticator;
import com.stormpath.sdk.error.authc.AccessTokenOAuthException;
import com.stormpath.sdk.impl.okta.DefaultOktaAccessTokenResult;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.error.DefaultError;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.impl.okta.OktaApiPaths;
import com.stormpath.sdk.impl.provider.DefaultOktaProviderAccountResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.OktaProviderData;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.stormpath.sdk.error.authc.AccessTokenOAuthException.INVALID_ACCESS_TOKEN;


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

        OktaTokenResponse oktaTokenResponse = dataStore.create(OktaApiPaths.oauthPath("token"), tokenRequest, OktaTokenResponse.class, getHeaders());

        // check if access key is valid
        TokenIntrospectResponse tokenIntrospectResponse = resolveAccessToken(oktaTokenResponse.getAccessToken());

        String userHref = OktaApiPaths.apiPath("users", tokenIntrospectResponse.getUid());

        return new DefaultOktaProviderAccountResult(dataStore.getResource(userHref, Account.class), oktaTokenResponse);
    }

    @Override
    public Account getAccountByToken(String accountToken) {

        // check if access key is valid
        TokenIntrospectResponse tokenIntrospectResponse = resolveAccessToken(accountToken);
        String userHref = OktaApiPaths.apiPath("users", tokenIntrospectResponse.getUid());
        return dataStore.getResource(userHref, Account.class);
    }

    @Override
    public AuthenticationResult authenticate(AuthenticationRequest request) {

        Assert.isInstanceOf(DefaultUsernamePasswordRequest.class, request, "Only 'DefaultUsernamePasswordRequest' requests are supported.");
        DefaultUsernamePasswordRequest usernamePasswordRequest = (DefaultUsernamePasswordRequest) request;

        final OktaTokenResponse oktaTokenResponse = doAuthRequest(usernamePasswordRequest);

        // check if access key is valid
        TokenIntrospectResponse tokenIntrospectResponse = resolveAccessToken(oktaTokenResponse.getAccessToken());

        String userHref = OktaApiPaths.apiPath("users", tokenIntrospectResponse.getUid());

        return new DefaultOktaAccessTokenResult(oktaTokenResponse, dataStore.getResource(userHref, Account.class));
    }

    public TokenIntrospectResponse resolveAccessToken(String accessToken) {

        TokenIntrospectRequest request = dataStore.instantiate(TokenIntrospectRequest.class)
            .setToken(accessToken)
            .setTokenTypeHint("access_token");

        TokenIntrospectResponse tokenIntrospectResponse = dataStore.create(OktaApiPaths.oauthPath("introspect"), request, TokenIntrospectResponse.class, getHeaders());

        // fail if token is invalid
        assertValidAccessToken(tokenIntrospectResponse);

        return tokenIntrospectResponse;
    }

    public OAuthGrantRequestAuthenticationResult resolveRefreshToken(String refreshToken, OAuthRefreshTokenRequestAuthenticator refreshTokenAuthenticator) {

        OAuthRequestAuthentication authenticationRequest = new DefaultOAuthRefreshTokenRequestAuthentication(refreshToken);
        return refreshTokenAuthenticator.authenticate(authenticationRequest);
    }

    private void assertValidAccessToken(TokenIntrospectResponse tokenResponse) {

        if(!tokenResponse.isActive()) {
            throw ApiAuthenticationExceptionFactory
                    .newOAuthException(AccessTokenOAuthException.class, INVALID_ACCESS_TOKEN);
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

            return this.dataStore.create(OktaApiPaths.oauthPath("token"), tokenRequest, OktaTokenResponse.class, getHeaders());
        }
        catch (final ResourceException e) {

            log.debug("Exception thrown while requesting token, assuming this is an Invalid username or password", e);

            // TODO: i18n? This String is also hard coded in the Spring Controller
            String errorMessage = "Invalid username or password.";

            // wrap Error so we can set the Message so it is handled correctly on the front end.
            throw new ResourceException(
                new DefaultError()
                    .setMessage(errorMessage)
                    .setCode(e.getCode())
                    .setDeveloperMessage(e.getDeveloperMessage())
                    .setMoreInfo(e.getMoreInfo())
                    .setRequestId(e.getRequestId())
                    .setStatus(e.getStatus())
            );
        }
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
