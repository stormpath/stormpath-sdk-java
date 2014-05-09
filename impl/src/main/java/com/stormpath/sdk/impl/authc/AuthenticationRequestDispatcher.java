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
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.RC
 */
public class AuthenticationRequestDispatcher {

    public AuthenticationResult authenticate(InternalDataStore dataStore, Application application, AuthenticationRequest request) {
        Assert.notNull(dataStore);
        Assert.notNull(application);
        Assert.notNull(request);

        if (request instanceof UsernamePasswordRequest) {
            return new BasicAuthenticator(dataStore).authenticate(application.getHref(), request);
        }

        if (request instanceof BasicApiAuthenticationRequest) {
            return new BasicApiAuthenticator(dataStore).authenticate(application, (BasicApiAuthenticationRequest) request);
        }

        throw new UnsupportedOperationException("The ");
    }
}
