package com.stormpath.sdk.oauth;

public interface CreateGoogleProviderRequestBuilder<T extends CreateGoogleProviderRequestBuilder<T>> extends CreateProviderRequestBuilder<T> {

    T setRedirectUri(String redirectUri);

}