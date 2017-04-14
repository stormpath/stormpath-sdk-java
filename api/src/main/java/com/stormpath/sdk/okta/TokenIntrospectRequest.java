package com.stormpath.sdk.okta;

import com.stormpath.sdk.resource.Resource;

/**
 *
 */
public interface TokenIntrospectRequest extends Resource {

    String getToken();
    TokenIntrospectRequest setToken(String token);

    String getTokenTypeHint();
    TokenIntrospectRequest setTokenTypeHint(String tokenTypeHint);

}
