package com.stormpath.sdk.application.okta;

import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.resource.Resource;

/**
 *
 */
public interface OktaTokenResponse extends Resource, TokenResponse {

    String getAccessToken();

    String getTokenType();

    String getExpiresIn();

    String getScope();

    String getRefreshToken();

}
