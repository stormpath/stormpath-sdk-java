package com.stormpath.sdk.provider;

import com.stormpath.sdk.oauth.TokenResponse;

/**
 */
public interface OktaProviderAccountResult extends ProviderAccountResult {

    TokenResponse getTokenResponse();
}
