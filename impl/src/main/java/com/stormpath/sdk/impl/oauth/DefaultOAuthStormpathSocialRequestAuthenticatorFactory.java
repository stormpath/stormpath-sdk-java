package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.application.DefaultApplication;
import com.stormpath.sdk.oauth.OAuthStormpathSocialGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthStormpathSocialRequestAuthenticatorFactory;

/**
 * @since 1.0.0
 */
public class DefaultOAuthStormpathSocialRequestAuthenticatorFactory implements OAuthStormpathSocialRequestAuthenticatorFactory {
    @Override
    public OAuthStormpathSocialGrantRequestAuthenticator forApplication(Application application) {
        return ((DefaultApplication) application).createStormpathSocialGrantAuthenticator();
    }
}
