package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountRequestBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

abstract class AbstractProviderAccountRequestBuilder<T extends ProviderAccountRequestBuilder<T>> implements ProviderAccountRequestBuilder<T> {

    protected String accessToken;

    public T setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return (T) this;
    }

    public ProviderAccountRequest build() {
        final String providerId = getProviderId();
        Assert.state(Strings.hasText(providerId), "The providerId property is missing.");

        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put("providerId", providerId);

        return doBuild(Collections.unmodifiableMap(properties));
    }

    protected abstract String getProviderId();
    protected abstract ProviderAccountRequest doBuild(Map<String, Object> map);

}
