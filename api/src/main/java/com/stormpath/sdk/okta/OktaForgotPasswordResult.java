package com.stormpath.sdk.okta;

import com.stormpath.sdk.resource.Resource;

/**
 *
 */
public interface OktaForgotPasswordResult extends Resource {

    String getStatus();
    String getFactorResult();
    String getRelayState();
    String getFactorType();
    String getRecoveryType();

}
