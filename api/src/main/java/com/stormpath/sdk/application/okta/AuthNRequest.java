package com.stormpath.sdk.application.okta;

import com.stormpath.sdk.resource.Resource;

import java.util.Map;

/**
 * Representation of /api/v1/authn
 */
public interface AuthNRequest extends Resource {

    String getUsername();
    AuthNRequest setUsername(String username);

    String getPassword();
    AuthNRequest setPassword(String password);

    Map<String, Object> getOptions();
    AuthNRequest setOptions(Map<String, Object> options);

}
