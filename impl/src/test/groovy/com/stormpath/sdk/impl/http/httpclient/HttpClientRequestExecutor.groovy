/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.impl.http.httpclient

import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.ClientCredentials
import com.stormpath.sdk.client.Proxy
import com.stormpath.sdk.http.Request
import com.stormpath.sdk.impl.http.RequestExecutor
import com.stormpath.sdk.impl.http.Response
import com.stormpath.sdk.http.RestException
import com.stormpath.sdk.authc.RequestAuthenticatorFactory

/**
 *
 * ### For testing purposes only ###
 *
 * {@code RequestExecutor} stub to allow a DefaultClient to be instantiated in a unit test.
 *
 * @since 1.0.0
 */
public class HttpClientRequestExecutor implements RequestExecutor {

    @Override
    public Response executeRequest(Request request) throws RestException {
        return null;
    }

    public HttpClientRequestExecutor(ClientCredentials clientCredentials, Proxy proxy, AuthenticationScheme authenticationScheme, RequestAuthenticatorFactory requestAuthenticatorFactory, Integer connectionTimeout) {

    }
}
