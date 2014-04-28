package com.stormpath.sdk.provider;

import com.stormpath.sdk.lang.Assert;

public abstract class AbstractProviderAccountRequest implements ProviderAccountRequest {

    ProviderData providerData;

    protected AbstractProviderAccountRequest(ProviderData providerData) {
        Assert.notNull(providerData, "providerData cannot be null");
        Assert.hasText(providerData.getProviderId(), "providerId within ProviderData instance must be specified");
        this.providerData = providerData;
    }

    @Override
    public ProviderData getProviderData() {
        return this.providerData;
    }

}