package com.stormpath.sdk.saml;

import com.stormpath.sdk.resource.Resource;

import java.util.Date;

public interface AuthnVerification extends Resource {
    String getRelayState();
    RegisteredSamlServiceProvider getServiceProvider();
    String getRequestId();
    Date getAuthnIssueInstant();
}
