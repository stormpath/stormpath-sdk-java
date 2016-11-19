package com.stormpath.sdk.saml;

import com.stormpath.sdk.resource.Resource;

public interface AuthnVerification extends Resource {
    String getRelayState();
    RegisteredSamlServiceProvider getServiceProvider();
    String getRequestId();
}
