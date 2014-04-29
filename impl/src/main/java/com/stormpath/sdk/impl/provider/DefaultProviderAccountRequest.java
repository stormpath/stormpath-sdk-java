package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderData;

public class DefaultProviderAccountRequest implements ProviderAccountRequest {

    ProviderData providerData;

    protected DefaultProviderAccountRequest(ProviderData providerData) {
        Assert.notNull(providerData, "providerData cannot be null");
        Assert.hasText(providerData.getProviderId(), "providerId within ProviderData instance must be specified");
        this.providerData = providerData;
    }

    @Override
    public ProviderData getProviderData() {
        return this.providerData;
    }

}