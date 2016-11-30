package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.provider.CreateProviderRequest;
import com.stormpath.sdk.provider.TwitterCreateProviderRequestBuilder;

import java.util.Map;

/**
 * @since 1.3.0
 */
public class DefaultTwitterCreateProviderRequestBuilder extends AbstractCreateProviderRequestBuilder<TwitterCreateProviderRequestBuilder> implements TwitterCreateProviderRequestBuilder {

    @Override
    protected String getConcreteProviderId() {
        return IdentityProviderType.FACEBOOK.getNameKey();
    }

    @Override
    protected CreateProviderRequest doBuild(Map<String, Object> map) {
        DefaultTwitterProvider provider = new DefaultTwitterProvider(null, map);
        provider.setClientId(super.clientId);
        provider.setClientSecret(super.clientSecret);
        if(super.userInfoMappingRules != null) {
            provider.setUserInfoMappingRules(super.userInfoMappingRules);
        }

        return new DefaultCreateProviderRequest(provider);
    }
}
