package com.stormpath.sdk.provider;

public interface CreateGoogleProviderRequestBuilder<T extends CreateGoogleProviderRequestBuilder<T>> extends CreateProviderRequestBuilder<T> {

    T setRedirectUri(String redirectUri);

}