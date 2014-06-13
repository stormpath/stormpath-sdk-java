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
package com.stormpath.sdk.oauth;

import com.stormpath.sdk.api.ApiRequestAuthenticator;

/**
 * An OAuth-specific {@code ApiRequestAuthenticator} that implements the
 * <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> to allow customization of how
 * the authentication attempt is processed.  For example:
 * <p>
 * <pre>
 * AuthenticationResult result = {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * application.authenticateOauthRequest(httpRequest)}
 *     <b>{@link #using(ScopeFactory) .using(scopeFactory)}
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
     * Specifies the {@link ScopeFactory} to be used when generating a new Access Token as a result of authenticating
     * the OAuth request.
     *
     * <p><b>This method should only be called when the OAuth client is specifically requesting a new Access Token</b>,
     * for example, a request to your application's oauth token endpoint, e.g. {@code /oauth/token}</p>
     *
     * @param scopeFactory the {@link ScopeFactory} to be used for this authentication request.
     * @return a new {@link AccessTokenRequestAuthenticator} instance created with the current state of the
     *         this builder.
     */
    AccessTokenRequestAuthenticator using(ScopeFactory scopeFactory);

    /**
     * Specifies the <a href="http://en.wikipedia.org/wiki/Time_to_live">time to live</a> of this authentication request
     * in seconds.  If not specified, the default value is {@code 3600} (seconds) - i.e. 1 hour.
     *
     * <p><b>This method should only be called when the OAuth client is specifically requesting a new Access Token</b>,
     * for example, a request to your application's oauth token endpoint, e.g. {@code /oauth/token}</p>
     *
     * @param ttl the time to live (in seconds) of this authentication request.
     * @return a new {@link AccessTokenRequestAuthenticator} instance created with the current state of the
     *         this builder.
     */
    AccessTokenRequestAuthenticator withTtl(long ttl);

    /**
     * Specifies the request location(s) that will be checked when looking up the request's Access Token.  Unspecified
     * locations will not be checked.
     * <p>
     * If this method is not called, both the request header and body will be checked by default.
     * </p>
     *
     * <p>This method will return a new {@link ResourceRequestAuthenticator} with the current state of the this
     * builder.</p>
     *
     * @param locations the location(s) for the <code>Bearer</code>.
     * @return a new {@link ResourceRequestAuthenticator} instance created with the current state of the
     *         this builder.
     */
    ResourceRequestAuthenticator inLocation(RequestLocation... locations);

    /**
     * Executes this authentication request.
     *
     * @return the result of the authentication request in the form of a {@link OauthAuthenticationResult}.
     */
    OauthAuthenticationResult execute();

}
