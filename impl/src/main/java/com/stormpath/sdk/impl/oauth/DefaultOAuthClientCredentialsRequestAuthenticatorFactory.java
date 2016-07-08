package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.application.DefaultApplication;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthClientCredentialsRequestAuthenticatorFactory;

/**
 * @since 1.0.0
 */
public class DefaultOAuthClientCredentialsRequestAuthenticatorFactory implements OAuthClientCredentialsRequestAuthenticatorFactory {
    @Override
    public OAuthClientCredentialsGrantRequestAuthenticator forApplication(Application application) {
        return ((DefaultApplication) application).createClientCredentialsGrantAuthenticator();
    }
}
