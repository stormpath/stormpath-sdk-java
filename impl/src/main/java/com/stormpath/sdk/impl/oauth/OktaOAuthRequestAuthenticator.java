package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.OktaAuthNAuthenticator;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.okta.OktaApiPaths;
import com.stormpath.sdk.impl.okta.OktaSigningKeyResolver;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthRequestAuthentication;
import com.stormpath.sdk.okta.TokenIntrospectResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 */
public class OktaOAuthRequestAuthenticator extends AbstractOAuthRequestAuthenticator implements OAuthBearerRequestAuthenticator {

    private Boolean isLocalValidation = false;
    private final OktaAuthNAuthenticator oktaAuthNAuthenticator;
    private final OktaSigningKeyResolver oktaSigningKeyResolver;


    public OktaOAuthRequestAuthenticator(Application application,
                                         DataStore dataStore,
                                         OktaAuthNAuthenticator oktaAuthNAuthenticator,
                                         OktaSigningKeyResolver oktaSigningKeyResolver) {
        super(application, dataStore);
        this.oktaAuthNAuthenticator = oktaAuthNAuthenticator;
        this.oktaSigningKeyResolver = oktaSigningKeyResolver;
    }

    @Override
    public OAuthBearerRequestAuthenticator withLocalValidation() {
        this.isLocalValidation = Boolean.TRUE;
        return this;
    }

    @Override
    public OAuthBearerRequestAuthenticationResult authenticate(OAuthRequestAuthentication authenticationRequest) {

        Assert.notNull(application, "application cannot be null or empty");
        Assert.isInstanceOf(OAuthBearerRequestAuthentication.class, authenticationRequest, "authenticationRequest must be an instance of JwtAuthenticationRequest.");

        OAuthBearerRequestAuthentication bearerRequest = (OAuthBearerRequestAuthentication) authenticationRequest;

        Account account;

        if (this.isLocalValidation) {

            // During parsing, the JWT is validated for expiration, signature and tampering
            Claims claims = Jwts.parser()
                    .setSigningKeyResolver(oktaSigningKeyResolver)
                    .parseClaimsJws(bearerRequest.getJwt()).getBody();

            Assert.isTrue(claims.getIssuer().startsWith(dataStore.getBaseUrl()));

            String uid = claims.get("uid", String.class);
            String accountHref = OktaApiPaths.apiPath("users", uid);
            account = dataStore.getResource(accountHref, Account.class);
        }
        else {
            TokenIntrospectResponse tokenIntrospectResponse = oktaAuthNAuthenticator.resolveAccessToken(bearerRequest.getJwt());
            account = tokenIntrospectResponse.getAccount();
        }

        AccessToken accessToken = new SimpleIntrospectAccessToken(bearerRequest.getJwt(), account, application);

        return new DefaultOAuthBearerRequestAuthenticationResult(accessToken);

    }
}
