package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.provider.GenericOAuth2ProviderAccountRequestBuilder;
import com.stormpath.sdk.provider.ProviderData;

import java.util.Map;

/**
 * @since 1.3.0
 */
public class DefaultGenericOAuth2ProviderAccountRequestBuilder extends AbstractSocialProviderAccountRequestBuilder<GenericOAuth2ProviderAccountRequestBuilder> implements GenericOAuth2ProviderAccountRequestBuilder {

    private String providerId;

    public GenericOAuth2ProviderAccountRequestBuilder setProviderId(String providerId) {
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
        return new DefaultGenericOAuth2ProviderData(null, properties);
    }

}
