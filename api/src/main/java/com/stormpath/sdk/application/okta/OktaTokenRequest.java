package com.stormpath.sdk.application.okta;

import com.stormpath.sdk.resource.Resource;

/**
 *
 */
public interface OktaTokenRequest extends Resource {

    String getGrantType();
    OktaTokenRequest setGrantType(String grantType);

    String getRedirectUri();
    OktaTokenRequest setRedirectUri(String redirectUri);

    String getUsername();
    OktaTokenRequest setUsername(String username);

    String getPassword();
    OktaTokenRequest setPassword(String password);

    String getScope();
    OktaTokenRequest setScope(String scope);

}
