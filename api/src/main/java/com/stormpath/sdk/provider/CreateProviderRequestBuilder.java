package com.stormpath.sdk.provider;

public interface CreateProviderRequestBuilder<T extends CreateProviderRequestBuilder<T>> {

    T setClientId(String clientId);

    T setClientSecret(String clientSecret);

    Provider build();

}
