package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountRequestBuilder;
import com.stormpath.sdk.provider.ProviderData;

import java.util.HashMap;
import java.util.Map;

import static com.stormpath.sdk.lang.Strings.hasText;

/**
 * @since 1.2.0
 */
abstract class AbstractSocialProviderAccountRequestBuilder<T extends ProviderAccountRequestBuilder<T>> extends AbstractProviderAccountRequestBuilder<T> {

    @Override
    protected ProviderAccountRequest doBuild(Map<String, Object> map) {
        Assert.state(hasText(this.code) ^ hasText(super.accessToken), "Either accessToken or code must be provided before building.");
        Map<String, Object> properties = new HashMap<>(map);
        if (hasText(accessToken)) {
            properties.put("accessToken", accessToken);
        } else {
            properties.put("code", code);
        }
        ProviderData providerData = newProviderData(properties);

        return new DefaultProviderAccountRequest(providerData, redirectUri);
    }

    protected abstract ProviderData newProviderData(Map<String, Object> properties);
}
