package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.OAuthStormpathFactorChallengeGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthStormpathSocialGrantRequestAuthentication;

/**
 * @since 1.3.1
 */
public class DefaultOAuthStormpathFactorChallengeGrantRequestAuthentication implements OAuthStormpathFactorChallengeGrantRequestAuthentication {
    private final static String grant_type = "stormpath_factor_challenge";

    private String challenge;
    private String code;

    public DefaultOAuthStormpathFactorChallengeGrantRequestAuthentication(String challenge, String code) {
        Assert.hasText(challenge, "challenge cannot be null or empty.");
        Assert.hasText(code, "code cannot be null or empty.");

        this.challenge = challenge;
        this.code = code;
    }

    @Override
    public String getChallenge() {
        return challenge;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getGrantType() {
        return grant_type;
    }
}
