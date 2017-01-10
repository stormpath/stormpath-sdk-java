package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.provider.GenericOAuth2CreateProviderRequestBuilder;
import com.stormpath.sdk.provider.GenericOAuth2ProviderAccountRequestBuilder;
import com.stormpath.sdk.provider.GenericOAuth2ProviderRequestFactory;

/**
 * @since 1.3.0
 */
public class DefaultGenericOAuth2ProviderRequestFactory implements GenericOAuth2ProviderRequestFactory {

    @Override
    public GenericOAuth2ProviderAccountRequestBuilder account() {
        return (GenericOAuth2ProviderAccountRequestBuilder) Classes.newInstance("com.stormpath.sdk.impl.provider.DefaultGenericOAuth2ProviderAccountRequestBuilder");
    }

    @Override
    public GenericOAuth2CreateProviderRequestBuilder builder() {
        return (GenericOAuth2CreateProviderRequestBuilder) Classes.newInstance("com.stormpath.sdk.impl.provider.DefaultGenericOAuth2CreateProviderRequestBuilder");
    }

}
