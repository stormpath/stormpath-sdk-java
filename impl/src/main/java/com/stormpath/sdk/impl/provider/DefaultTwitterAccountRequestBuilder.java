package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.provider.ProviderData;
import com.stormpath.sdk.provider.TwitterAccountRequestBuilder;

import java.util.Map;

/**
 * @since 1.3.0
 */
public class DefaultTwitterAccountRequestBuilder extends AbstractSocialProviderAccountRequestBuilder<TwitterAccountRequestBuilder> implements TwitterAccountRequestBuilder {

    protected String accessTokenSecret;

    @Override
    protected String getConcreteProviderId() {
        return IdentityProviderType.TWITTER.getNameKey();
    }

    @Override
    protected ProviderData newProviderData(Map<String, Object> properties) {
        return new DefaultTwitterProviderData(null, properties);
    }

    @Override
    public TwitterAccountRequestBuilder setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
        return this;
    }
}
