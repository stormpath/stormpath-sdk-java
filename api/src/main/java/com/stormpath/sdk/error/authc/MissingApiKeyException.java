package com.stormpath.sdk.error.authc;

import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.resource.ResourceException;

/**
 * MissingApiKeyException
 *
 * @since 1.0.RC
 */
public class MissingApiKeyException extends ResourceException{
    public MissingApiKeyException(com.stormpath.sdk.error.Error error) {
        super(error);
    }
}
