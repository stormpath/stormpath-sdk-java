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

import com.stormpath.sdk.authc.ApiRequestAuthenticator;
import com.stormpath.sdk.oauth.authz.ScopeFactory;

/**
 * An OAuth-specific {@code ApiRequestAuthenticator} that implements the
 * <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> to allow customization of how
 * the authentication attempt is processed.  For example:
 * <p>
 * <pre>
 * AuthenticationResult result = {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object) application.authenticateOauthRequest(httpRequest)}
 *     <b>{@link #using(com.stormpath.sdk.oauth.authz.ScopeFactory) .using(scopeFactory)}
 *     {@link #withTtl(long) .withTtl(3600)}
 *     {@link #execute() .execute()};</b>
 * </pre>
 * </p>
 *
 * @see com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * @see #execute()
 * @since 1.0.RC
 */
public interface OauthRequestAuthenticator extends ApiRequestAuthenticator {

    /**
     * Specifies the {@link ScopeFactory} to be used for this authentication request.
     * <p/>
     * Note that this method will return a new {@link TokenOauthRequestAuthenticator} with the current state of the
     * this builder.
     *
     * @param scopeFactory the {@link ScopeFactory} to be used for this authentication request.
     * @return a new {@link TokenOauthRequestAuthenticator} instance created with the current state of the
     *         this builder.
     */
    TokenOauthRequestAuthenticator using(ScopeFactory scopeFactory);

    /**
     * Specifies the <a href="http://en.wikipedia.org/wiki/Time_to_live">time to live</a> of this authentication request in
     * seconds.  If not specified, the default value is {@code 3600} (seconds) - i.e. 1 hour.
     * <p/>
     * Note that this method will return a new {@link TokenOauthRequestAuthenticator} with the current state of the
     * this builder.
     *
     * @param ttl the time to live (in seconds) of this authentication request.
     * @return a new {@link TokenOauthRequestAuthenticator} instance created with the current state of the
     *         this builder.
     */
    TokenOauthRequestAuthenticator withTtl(long ttl);

    /**
     * Specifies the location(s) where the <code>Bearer</code> shall be placed.
     * <p/>
     * Note that this method will return a new {@link BearerOauthRequestAuthenticator} with the current state of the
     * this builder.
     *
     * @param locations the location(s) for the <code>Bearer</code>.
     * @return a new {@link BearerOauthRequestAuthenticator} instance created with the current state of the
     *         this builder.
     */
    BearerOauthRequestAuthenticator inLocation(RequestLocation... locations);

    /**
     * Executes this authentication request.
     *
     * @return the result of the authentication request in the form of a {@link OauthAuthenticationResult}.
     */
    OauthAuthenticationResult execute();

}
