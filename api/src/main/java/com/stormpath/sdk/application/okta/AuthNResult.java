package com.stormpath.sdk.application.okta;

import com.stormpath.sdk.resource.Resource;

import java.util.Map;

/**
 * Representation of /api/v1/authn
 */
public interface AuthNResult extends Resource {

    String getSessionToken();

    String getUserId();

}
