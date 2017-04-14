package com.stormpath.sdk.okta;

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

    String getCode();
    OktaTokenRequest setCode(String code);

    String getScope();
    OktaTokenRequest setScope(String scope);

}
