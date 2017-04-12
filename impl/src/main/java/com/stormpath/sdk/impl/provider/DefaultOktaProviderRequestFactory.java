package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.provider.OktaAccountRequestBuilder;
import com.stormpath.sdk.provider.OktaCreateProviderRequestBuilder;
import com.stormpath.sdk.provider.OktaRequestFactory;

/**
 */
public class DefaultOktaProviderRequestFactory implements OktaRequestFactory {
    @Override
    public OktaAccountRequestBuilder account() {
        return (OktaAccountRequestBuilder) Classes.newInstance("com.stormpath.sdk.impl.provider.DefaultOktaAccountRequestBuilder");
    }

    @Override
    public OktaCreateProviderRequestBuilder builder() {
        return (OktaCreateProviderRequestBuilder) Classes.newInstance("com.stormpath.sdk.impl.provider.DefaultOktaCreateProviderRequestBuilder");
    }
}
