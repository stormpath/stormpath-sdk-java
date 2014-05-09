package com.stormpath.sdk.error.authc;

import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.resource.ResourceException;

/**
 * @since 1.0.RC
 */
public class IncorrectCredentialsException extends ResourceException {

    public IncorrectCredentialsException(Error error) {
        super(error);
    }

}
