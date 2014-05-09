package com.stormpath.sdk.error.authc;

import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.resource.ResourceException;


/**
 * DisabledApiKeyException
 *
 * @since 1.0.RC
 */
public class DisabledApiKeyException extends ResourceException {

    public DisabledApiKeyException(Error error) {
        super(error);
    }

}
