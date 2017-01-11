package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.application.DefaultApplication;
import com.stormpath.sdk.oauth.OAuthStormpathFactorChallengeGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthStormpathFactorChallengeGrantRequestAuthenticatorFactory;

/**
 * @since 1.3.1
 */
public class DefaultOAuthStormpathFactorChallengeRequestAuthenticatorFactory implements OAuthStormpathFactorChallengeGrantRequestAuthenticatorFactory {
    @Override
    public OAuthStormpathFactorChallengeGrantRequestAuthenticator forApplication(Application application) {
        return ((DefaultApplication) application).createStormpathFactorChallengeGrantAuthenticator();
    }
}
