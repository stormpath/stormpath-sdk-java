package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderData;
import com.stormpath.sdk.provider.TwitterAccountRequestBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.stormpath.sdk.lang.Strings.hasText;

/**
 * @since 1.3.0
 */
public class DefaultTwitterAccountRequestBuilder extends AbstractSocialProviderAccountRequestBuilder<TwitterAccountRequestBuilder> implements TwitterAccountRequestBuilder {

    protected String accessTokenSecret;

    @Override
    protected ProviderAccountRequest doBuild(Map<String, Object> map) {
        Assert.state(hasText(super.accessToken) && hasText(this.accessTokenSecret), "Both accessToken and accessTokenSecret must be provided before building.");
        Map<String, Object> properties = new HashMap<>(map);
        properties.put("accessToken", accessToken);
        properties.put("accessTokenSecret", accessTokenSecret);

        ProviderData providerData = newProviderData(properties);

        return new DefaultProviderAccountRequest(providerData, redirectUri);
    }

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
