package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.Provider;

/**
 * @since 1.0.0
 */
public class DefaultProviderModel implements ProviderModel {

    private final String href;
    private final String providerId;

    public DefaultProviderModel(Provider provider) {
        this(provider.getHref(), provider.getProviderId());
    }

    public DefaultProviderModel(String href, String providerId) {
        Assert.hasText(href, "provider href cannot be null or empty.");
        Assert.hasText(providerId, "provider providerId cannot be null or empty.");
        this.href = href;
        this.providerId = providerId;
    }

    @Override
    public String getHref() {
        return this.href;
    }

    @Override
    public String getProviderId() {
        return this.providerId;
    }
}
