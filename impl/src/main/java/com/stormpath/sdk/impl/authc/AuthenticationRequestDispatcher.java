/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.*;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.RC
 */
public class AuthenticationRequestDispatcher {

    protected static final String UNSUPPORTED_AUTH_REQUEST_MSG = "The AuthenticationRequest [%s] is not supported by this implementation.";

    public AuthenticationResult authenticate(InternalDataStore dataStore, Application application, AuthenticationRequest request, AuthenticationOptions options) {
        Assert.notNull(dataStore, "datastore cannot be null.");
        Assert.notNull(application, "application cannot be null.");
        Assert.notNull(request, "request cannot be null.");

        if (request instanceof UsernamePasswordRequest) {
            Assert.isTrue(options == null || options instanceof BasicAuthenticationOptions, "options must be an instance of BasicAuthenticationOptions.");
            return new BasicAuthenticator(dataStore).authenticate(application.getHref(), request, (BasicAuthenticationOptions) options);
        }

        if (request instanceof DefaultBasicApiAuthenticationRequest) {
            Assert.isTrue(options == null, "expansion is not supported for ApiAuthenticationRequests.");
            return new BasicApiAuthenticator(dataStore).authenticate(application, (DefaultBasicApiAuthenticationRequest) request);
        }

        throw new UnsupportedOperationException(String.format(UNSUPPORTED_AUTH_REQUEST_MSG, request.getClass().getName()));
    }
}
