package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.impl.okta.OktaSigningKeyResolver;
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
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.stormpath.sdk.error.authc.AccessTokenOAuthException.INVALID_ACCESS_TOKEN;


/**
 * Uses Okta's /api/v1/authn endpoint to authenticate users.
 */
public class DefaultOktaAuthNAuthenticator implements OktaAuthNAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(DefaultOktaAuthNAuthenticator.class);

    private final InternalDataStore dataStore;
    private final OktaSigningKeyResolver signingKeyResolver;
    private final String tokenUrl;
    private final String introspectUrl;

    public DefaultOktaAuthNAuthenticator(InternalDataStore dataStore, String tokenUrl, String introspectUrl, OktaSigningKeyResolver signingKeyResolver) {

        Assert.notNull(dataStore);

        this.tokenUrl = tokenUrl;
        this.introspectUrl = introspectUrl;
        this.dataStore = dataStore;
        this.signingKeyResolver = signingKeyResolver;
    }

    @Override
    public String getHref() {
        return null;
    }

    @Override
    public ProviderAccountResult getAccount(ProviderAccountRequest request) {

        OktaProviderData oktaProviderData = (OktaProviderData) request.getProviderData();

        OktaTokenRequest tokenRequest = dataStore.instantiate(OktaTokenRequest.class);
        tokenRequest.setCode(oktaProviderData.getCode());
        tokenRequest.setGrantType("authorization_code");
        tokenRequest.setRedirectUri(request.getRedirectUri());

        OktaTokenResponse oktaTokenResponse = dataStore.create(tokenUrl, tokenRequest, OktaTokenResponse.class, getHeaders());

        String uid = getUserId(oktaTokenResponse.getAccessToken());
        String userHref = OktaApiPaths.apiPath("users", uid);

        return new DefaultOktaProviderAccountResult(dataStore.getResource(userHref, Account.class), oktaTokenResponse);
    }

    @Override
    public Account getAccountByToken(String accountToken) {

        String uid = getUserId(accountToken);
        String userHref = OktaApiPaths.apiPath("users", uid);
        return dataStore.getResource(userHref, Account.class);
    }

    @Override
    public AuthenticationResult authenticate(AuthenticationRequest request) {

        Assert.isInstanceOf(DefaultUsernamePasswordRequest.class, request, "Only 'DefaultUsernamePasswordRequest' requests are supported.");
        DefaultUsernamePasswordRequest usernamePasswordRequest = (DefaultUsernamePasswordRequest) request;

        final OktaTokenResponse oktaTokenResponse = doAuthRequest(usernamePasswordRequest);

        String uid = getUserId(oktaTokenResponse.getAccessToken());
        String userHref = OktaApiPaths.apiPath("users", uid);

        return new DefaultOktaAccessTokenResult(oktaTokenResponse, dataStore.getResource(userHref, Account.class));
    }

    private String getUserId(String token) {

        // if we have a signing key resolver use it to validate the token locally
        // otherwise hit the introspect endpoint
        if (signingKeyResolver != null) {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKeyResolver(signingKeyResolver)
                    .parseClaimsJws(token);
            return claims.getBody().get("uid", String.class);
        }

        return resolveAccessToken(token).getUid();
    }

    private TokenIntrospectResponse resolveAccessToken(String accessToken) {

        TokenIntrospectRequest request = dataStore.instantiate(TokenIntrospectRequest.class)
            .setToken(accessToken)
            .setTokenTypeHint("access_token");

        TokenIntrospectResponse tokenIntrospectResponse = dataStore.create(introspectUrl, request, TokenIntrospectResponse.class, getHeaders());

        // fail if token is invalid
        assertValidAccessToken(tokenIntrospectResponse);

        return tokenIntrospectResponse;
    }

    private void assertValidAccessToken(TokenIntrospectResponse tokenResponse) {

        if(!tokenResponse.isActive()) {
            throw ApiAuthenticationExceptionFactory
                    .newOAuthException(AccessTokenOAuthException.class, INVALID_ACCESS_TOKEN);
        }
    }

    private OktaTokenResponse doAuthRequest(DefaultUsernamePasswordRequest request) {

        // hit the oauth token endpoint
        OktaTokenRequest tokenRequest = this.dataStore.instantiate(OktaTokenRequest.class)
                .setPassword(new String(request.getCredentials()))
                .setUsername(request.getPrincipals())
                .setGrantType("password")
                .setScope("offline_access");

        try {
            return this.dataStore.create(tokenUrl, tokenRequest, OktaTokenResponse.class, getHeaders());
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

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return httpHeaders;
    }
}
