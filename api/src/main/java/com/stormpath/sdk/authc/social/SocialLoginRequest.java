package com.stormpath.sdk.authc.social;

public interface SocialLoginRequest {

    String getTokenType();

    String getTokenValue();

    SocialLoginProvider getProvider();

}
