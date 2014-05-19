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
package com.stormpath.sdk.oauth.authc;

import com.stormpath.sdk.oauth.permission.ScopeFactory;

/**
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> used to
 * construct an executable {@link com.stormpath.sdk.authc.AuthenticationRequest AuthenticationRequest} instance.
 * <p/>
 * <pre>
 *     {@code AuthenticationResult authResult = }{@link com.stormpath.sdk.application.Application#authenticateOauth(Object) application.authenticateOauth(httpRequest)}{@code .withTtl(1000).execute()};
 * </pre>
 *
 * @see com.stormpath.sdk.application.Application#authenticateOauth(Object)
 * @see #execute()
 * @since 1.0.RC
 */
public interface BasicOauthAuthenticationRequestBuilder {

    /**
     * Specifies the {@link ScopeFactory} to be used for this authentication request.
     *
     * @param scopeFactory the {@link ScopeFactory} to be used for this authentication request.
     * @return this instance for method chaining.
     */
    public BasicOauthAuthenticationRequestBuilder using(ScopeFactory scopeFactory);

    /**
     * Specifies the <a href="http://en.wikipedia.org/wiki/Time_to_live">time to live</a> of this authentication request
     * in seconds.
     *
     * @param ttl the time to live (in seconds) of this authentication request.
     * @return this instance for method chaining.
     */
    public BasicOauthAuthenticationRequestBuilder withTtl(long ttl);

    /**
     * Executes this authentication request.
     *
     * @return the result of the authentication request in the form of a {@link BasicOauthAuthenticationResult}.
     */
    public BasicOauthAuthenticationResult execute() throws Exception;
}
