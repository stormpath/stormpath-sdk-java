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

import com.stormpath.sdk.authc.ApiAuthenticationRequestBuilder;
import com.stormpath.sdk.oauth.authz.ScopeFactory;

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
public interface OauthAuthenticationRequestBuilder extends ApiAuthenticationRequestBuilder {

    /**
     * Specifies the {@link ScopeFactory} to be used for this authentication request.
     * <p/>
     * Note that this method will return a new {@link BasicOauthAuthenticationRequestBuilder} with the current state of the
     * this builder.
     *
     * @param scopeFactory the {@link ScopeFactory} to be used for this authentication request.
     * @return a new {@link BasicOauthAuthenticationRequestBuilder} instance created with the current state of the
     *         this builder.
     */
    BasicOauthAuthenticationRequestBuilder using(ScopeFactory scopeFactory);

    /**
     * Specifies the <a href="http://en.wikipedia.org/wiki/Time_to_live">time to live</a> of this authentication request in
     * seconds.
     * <p/>
     * Note that this method will return a new {@link BasicOauthAuthenticationRequestBuilder} with the current state of the
     * this builder.
     *
     * @param ttl the time to live (in seconds) of this authentication request.
     * @return a new {@link BasicOauthAuthenticationRequestBuilder} instance created with the current state of the
     *         this builder.
     */
    BasicOauthAuthenticationRequestBuilder withTtl(long ttl);

    /**
     * Specifies the location(s) where the <code>Bearer</code> shall be placed.
     * <p/>
     * Note that this method will return a new {@link BearerOauthAuthenticationRequestBuilder} with the current state of the
     * this builder.
     *
     * @param locations the location(s) for the <code>Bearer</code>.
     * @return a new {@link BearerOauthAuthenticationRequestBuilder} instance created with the current state of the
     *         this builder.
     */
    BearerOauthAuthenticationRequestBuilder inLocation(BearerLocation... locations);

    /**
     * Executes this authentication request.
     *
     * @return the result of the authentication request in the form of a {@link OauthAuthenticationResult}.
     */
    OauthAuthenticationResult execute();

}
