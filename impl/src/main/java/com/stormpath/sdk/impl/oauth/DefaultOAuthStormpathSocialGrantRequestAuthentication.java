package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.OAuthStormpathSocialGrantRequestAuthentication;

/**
 * @since 1.1.0
 */
public class DefaultOAuthStormpathSocialGrantRequestAuthentication implements OAuthStormpathSocialGrantRequestAuthentication {
    private final static String grant_type = "stormpath_social";

    private String providerId;
    private String accessToken;
    private String code;

    public DefaultOAuthStormpathSocialGrantRequestAuthentication(String providerId, String accessToken, String code) {
        Assert.hasText(providerId, "providerId cannot be null or empty.");
        if (code == null) {
            Assert.hasText(accessToken, "accessToken cannot be null or empty.");
        }
        if (accessToken == null) {
            Assert.hasText(code, "code cannot be null or empty.");
        }

        this.providerId = providerId;
        this.accessToken = accessToken;
        this.code = code;
    }

    @Override
    public String getProviderId() {
        return providerId;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
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
