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
package com.stormpath.sdk.authc;

import com.stormpath.sdk.client.ClientCredentials;
import com.stormpath.sdk.http.Request;

/**
 * Interface to be implemented by HTTP authentication schemes. Such scheme defines the way the communication with
 * the Stormpath API server will be authenticated.
 *
 * @see com.stormpath.sdk.client.AuthenticationScheme
 * @since 0.9.3
 */
public interface RequestAuthenticator {
    String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Implementations of this operation will prepare the authentication information as expected by the Stormpath API server.
     *
     * @param request the request that will be sent to Stormpath API server, it shall be modified by the implementating classes
     *                in order to insert here the authentication information
     * @param clientCredentials provides the authentication data that will be used to create the proper authentication information for
     *               the specific scheme the implementation defines.
     * @throws RequestAuthenticationException when the authentication request cannot be created
     */
    void authenticate(Request request, ClientCredentials clientCredentials) throws RequestAuthenticationException;

}
