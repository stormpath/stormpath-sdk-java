package com.stormpath.sdk.error.authc;

import com.stormpath.sdk.error.*;
import com.stormpath.sdk.resource.ResourceException;

/**
 * InvalidApiKeyException
 *
 * @since 0.49
 */
public class InvalidApiKeyException extends ResourceException {

    public InvalidApiKeyException(com.stormpath.sdk.error.Error error) {
        super(error);
    }
}
