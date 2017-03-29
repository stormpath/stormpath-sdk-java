package com.stormpath.sdk.authc;

import com.stormpath.sdk.resource.Resource;

/**
 *
 */
public interface OktaAuthNAuthenticator extends Resource {

    AuthenticationResult authenticate(AuthenticationRequest request);
    
    void assertValidAccessToken(String accessToken);
}
