package com.stormpath.sdk.application.okta;

import com.stormpath.sdk.resource.Resource;

/**
 *
 */
public interface OktaForgotPasswordRequest extends Resource {

    String getUsername();
    OktaForgotPasswordRequest setUsername(String username);

    String getFactorType();
    OktaForgotPasswordRequest setFactorType(String factorType);

    String getRelayState();
    OktaForgotPasswordRequest setRelayState(String relayState);
}
