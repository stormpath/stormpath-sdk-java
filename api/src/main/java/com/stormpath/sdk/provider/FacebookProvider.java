package com.stormpath.sdk.provider;

public interface FacebookProvider extends Provider {

    String getClientId();

    FacebookProvider setClientId(String clientId);

    String getClientSecret();

    FacebookProvider setClientSecret(String clientSecret);

}
