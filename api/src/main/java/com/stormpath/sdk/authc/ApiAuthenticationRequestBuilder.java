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
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> used to
 * construct an executable {@link AuthenticationRequest} instance.
 * <p/>
 * <pre>
 *     {@code AuthenticationResult authResult = }{@link com.stormpath.sdk.application.Application#authenticate(Object) application.authenticate(httpRequest)}{@code .execute()};
 * </pre>
 *
 * @see com.stormpath.sdk.application.Application#authenticate(Object)
 * @see #execute()
 * @since 1.0.RC
 */
public interface ApiAuthenticationRequestBuilder {

    /**
     * Returns an {@link ApiAuthenticationResult ApiAuthenticationResult} after a successful {@link AuthenticationRequest}.
     * <p/>
     * The concrete type of the authentication result will depend on the request type; for example: {@code Api}, {@code BearerOauth}
     * or {@code BasicOauth}.
     *
     * @return If authentication was successful an {@link ApiAuthenticationResult} instance.
     *
     * @see com.stormpath.sdk.application.Application#authenticateOauth(Object)
     * @see com.stormpath.sdk.application.Application#authenticate(Object)
     */
    ApiAuthenticationResult execute();
}
