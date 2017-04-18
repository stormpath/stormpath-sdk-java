package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.OktaAuthNAuthenticator;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.oauth.GrantAuthenticationToken;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthTokenRevocator;

/**
 */
public class OktaOAuthRefreshTokenRequestAuthenticator extends DefaultOAuthRefreshTokenRequestAuthenticator {

    private final OktaAuthNAuthenticator authenticator;
    private final OAuthTokenRevocator tokenRevocator;

    public OktaOAuthRefreshTokenRequestAuthenticator(DataStore dataStore, String oauthTokenUrl, OktaAuthNAuthenticator authenticator, OAuthTokenRevocator tokenRevocator) {
        super(dataStore, oauthTokenUrl);
        this.authenticator = authenticator;
        this.tokenRevocator = tokenRevocator;
    }

    @Override
    protected OAuthGrantRequestAuthenticationResult buildGrant(GrantAuthenticationToken grantResult) {
        OAuthGrantRequestAuthenticationResultBuilder builder = new OktaOAuthGrantRequestAuthenticationResultBuilder(grantResult, authenticator, tokenRevocator, null, application);
        return builder.build();
    }
}
