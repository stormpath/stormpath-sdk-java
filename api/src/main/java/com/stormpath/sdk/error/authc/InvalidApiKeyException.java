package com.stormpath.sdk.error.authc;

import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.resource.ResourceException;

/**
 * InvalidApiKeyException
 *
 * @since 1.0.RC
 */
public class InvalidApiKeyException extends ResourceException {

    public InvalidApiKeyException(Error error) {
        super(error);
    }
}
