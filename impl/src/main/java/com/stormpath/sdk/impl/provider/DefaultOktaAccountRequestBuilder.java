package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.provider.OktaAccountRequestBuilder;
import com.stormpath.sdk.provider.ProviderData;

import java.util.Map;

/**
 */
public class DefaultOktaAccountRequestBuilder extends AbstractSocialProviderAccountRequestBuilder<OktaAccountRequestBuilder> implements OktaAccountRequestBuilder {

    @Override
    protected String getConcreteProviderId() {
            return IdentityProviderType.OKTA.getNameKey();
    }

    @Override
    protected ProviderData newProviderData(Map<String, Object> properties) {
        return new DefaultOktaProviderData(null, properties);
    }
}
