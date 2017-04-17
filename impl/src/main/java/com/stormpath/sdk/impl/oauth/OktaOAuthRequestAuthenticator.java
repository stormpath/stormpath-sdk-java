package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.OktaAuthNAuthenticator;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthRequestAuthentication;
import com.stormpath.sdk.okta.TokenIntrospectResponse;

/**
 */
public class OktaOAuthRequestAuthenticator extends AbstractOAuthRequestAuthenticator implements OAuthBearerRequestAuthenticator {

    private Boolean isLocalValidation = false;
    private final OktaAuthNAuthenticator oktaAuthNAuthenticator;


    public OktaOAuthRequestAuthenticator(Application application, DataStore dataStore, OktaAuthNAuthenticator oktaAuthNAuthenticator) {
        super(application, dataStore);
        this.oktaAuthNAuthenticator = oktaAuthNAuthenticator;
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

//        if (this.isLocalValidation) {} // FIXME: always remote to start with

        TokenIntrospectResponse tokenIntrospectResponse = oktaAuthNAuthenticator.resolveAccessToken(bearerRequest.getJwt());

        AccessToken accessToken = new SimpleIntrospectAccessToken(bearerRequest.getJwt(), tokenIntrospectResponse.getAccount(), application);

        return new DefaultOAuthBearerRequestAuthenticationResult(accessToken);

    }
}
