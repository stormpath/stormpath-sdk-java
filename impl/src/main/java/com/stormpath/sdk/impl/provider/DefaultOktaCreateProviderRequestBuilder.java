package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.provider.CreateProviderRequest;
import com.stormpath.sdk.provider.OktaCreateProviderRequestBuilder;

import java.util.Map;

/**
 */
public class DefaultOktaCreateProviderRequestBuilder extends AbstractCreateProviderRequestBuilder<OktaCreateProviderRequestBuilder> implements OktaCreateProviderRequestBuilder {

    @Override
    protected String getConcreteProviderId() {
        return IdentityProviderType.OKTA.getNameKey();
    }

    @Override
    protected CreateProviderRequest doBuild(Map<String, Object> map) {
        DefaultOktaProvider provider = new DefaultOktaProvider(null, map);
        provider.setClientId(super.clientId);
        provider.setClientSecret(super.clientSecret);
        if (super.userInfoMappingRules != null) {
            provider.setUserInfoMappingRules(super.userInfoMappingRules);
        }

        return new DefaultCreateProviderRequest(provider);
    }

    @Override
    public OktaCreateProviderRequestBuilder setRedirectUri(String redirectUri) {
        return null;
    }
}