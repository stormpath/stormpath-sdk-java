package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.provider.OAuth2ProviderAccountRequestBuilder;
import com.stormpath.sdk.provider.ProviderData;

import java.util.Map;

/**
 * @since 1.3.0
 */
public class DefaultOAuth2ProviderAccountRequestBuilder extends AbstractSocialProviderAccountRequestBuilder<OAuth2ProviderAccountRequestBuilder> implements OAuth2ProviderAccountRequestBuilder {

    private String providerId;

    public OAuth2ProviderAccountRequestBuilder setProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public String getProviderId() {
        return this.providerId;
    }

    @Override
    protected String getConcreteProviderId() {
        return getProviderId();
    }

    @Override
    protected ProviderData newProviderData(Map<String, Object> properties) {
        return new DefaultOAuth2ProviderData(null, properties);
    }

}
