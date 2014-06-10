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

/**
 * Authenticates an API HTTP Request and returns a {@link ApiAuthenticationResult result}.
 *
 * @see com.stormpath.sdk.application.Application#authenticateApiRequest(Object)
 * @see #execute()
 * @since 1.0.RC
 */
public interface ApiRequestAuthenticator {

    /**
     * Returns an {@link ApiAuthenticationResult ApiAuthenticationResult} after a successful authentication to an
     * HTTP API endpoint.
     * <p>The concrete type of the authentication result will depend on the request type, and can be resolved to the
     * specific type using a {@link AuthenticationResultVisitor}.
     *
     * @return ApiAuthenticationResult if the API request was authenticated successfully.
     *
     * @see com.stormpath.sdk.application.Application#authenticateApiRequest(Object)
     * @see com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
     */
    ApiAuthenticationResult execute();
}
