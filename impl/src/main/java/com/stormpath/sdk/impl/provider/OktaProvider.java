package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.Provider;

import java.util.Date;

public class OktaProvider implements Provider {

    private final String baseUrl; //customer url
    private final Date createdAt;
    private final Date modifiedAt;

    public OktaProvider(String baseUrl, Date createdAt, Date modifiedAt) {
        Assert.hasText(baseUrl, "baseUrl cannot be null or empty.");
        this.baseUrl = baseUrl;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    @Override
    public String getHref() {
        return baseUrl;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public Date getModifiedAt() {
        return modifiedAt;
    }

    @Override
    public String getProviderId() {
        return "okta";
    }
}
