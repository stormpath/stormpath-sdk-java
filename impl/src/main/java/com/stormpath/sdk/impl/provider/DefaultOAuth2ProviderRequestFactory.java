package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.provider.OAuth2CreateProviderRequestBuilder;
import com.stormpath.sdk.provider.OAuth2ProviderAccountRequestBuilder;
import com.stormpath.sdk.provider.OAuth2ProviderRequestFactory;

/**
 * @since 1.3.0
 */
public class DefaultOAuth2ProviderRequestFactory implements OAuth2ProviderRequestFactory {

    @Override
    public OAuth2ProviderAccountRequestBuilder account() {
        return (OAuth2ProviderAccountRequestBuilder) Classes.newInstance("com.stormpath.sdk.impl.provider.DefaultOAuth2ProviderAccountRequestBuilder");
    }

    @Override
    public OAuth2CreateProviderRequestBuilder builder() {
        return (OAuth2CreateProviderRequestBuilder) Classes.newInstance("com.stormpath.sdk.impl.provider.DefaultOAuth2CreateProviderRequestBuilder");
    }

}
