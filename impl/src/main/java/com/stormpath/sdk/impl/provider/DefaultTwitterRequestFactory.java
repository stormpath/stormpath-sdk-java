package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.provider.TwitterAccountRequestBuilder;
import com.stormpath.sdk.provider.TwitterCreateProviderRequestBuilder;
import com.stormpath.sdk.provider.TwitterRequestFactory;

/**
 * @since 1.3.0
 */
public class DefaultTwitterRequestFactory implements TwitterRequestFactory {

    @Override
    public TwitterAccountRequestBuilder account() {
        return (TwitterAccountRequestBuilder) Classes.newInstance("com.stormpath.sdk.impl.provider.DefaultTwitterAccountRequestBuilder");
    }

    @Override
    public TwitterCreateProviderRequestBuilder builder() {
        return (TwitterCreateProviderRequestBuilder) Classes.newInstance("com.stormpath.sdk.impl.provider.DefaultTwitterCreateProviderRequestBuilder");
    }

}
