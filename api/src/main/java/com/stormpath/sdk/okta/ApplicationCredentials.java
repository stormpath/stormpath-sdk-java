package com.stormpath.sdk.okta;

import com.stormpath.sdk.resource.Resource;

/**
 *
 */
public interface ApplicationCredentials extends Resource {

    String getClientId();

    ApplicationCredentials setClientId(String clientId);

    String getClientSecret();

    ApplicationCredentials setClientSecret(String clientSecret);
}
