package com.stormpath.sdk.okta;

import com.stormpath.sdk.resource.Resource;

/**
 */
public interface OktaActivateAccountResponse extends Resource {

    String getActivationUrl();
    String getActivationToken();
}
