package com.stormpath.sdk.provider;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.TokenResponse;

/**
 */
public interface OktaProviderAccountResult extends ProviderAccountResult, AuthenticationResult, AccessTokenResult {

    TokenResponse getTokenResponse();
}
