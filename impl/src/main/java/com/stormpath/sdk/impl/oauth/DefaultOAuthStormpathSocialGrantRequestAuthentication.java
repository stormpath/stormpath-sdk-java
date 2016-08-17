package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.OAuthStormpathSocialGrantRequestAuthentication;

/**
 * @since 1.1.0
 */
public class DefaultOAuthStormpathSocialGrantRequestAuthentication implements OAuthStormpathSocialGrantRequestAuthentication {
    private final static String grant_type = "stormpath_social";

    private String apiKeyId;
    private String apiKeySecret;

    public DefaultOAuthStormpathSocialGrantRequestAuthentication(String apiKeyId, String apiKeySecret) {
        Assert.hasText(apiKeyId, "apiKeyId cannot be null or empty.");
        Assert.hasText(apiKeySecret, "apiKeySecret cannot be null or empty.");

        this.apiKeyId = apiKeyId;
        this.apiKeySecret = apiKeySecret;
    }

    @Override
    public String getApiKeyId() {
        return apiKeyId;
    }

    @Override
    public String getApiKeySecret() {
        return apiKeySecret;
    }

    @Override
    public String getGrantType() {
        return grant_type;
    }
}
