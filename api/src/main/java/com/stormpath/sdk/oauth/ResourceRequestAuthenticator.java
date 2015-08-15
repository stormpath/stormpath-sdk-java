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

import com.stormpath.sdk.http.HttpRequest;

/**
 * Authenticates a client request to an API resource (URI) endpoint based on the presence of an OAuth Access Token in
 * the request.  This interface reflects the
 * <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> to allow customization of how
 * the authentication attempt is processed.  For example:
 *
 * <pre>
 * AuthenticationResult result = {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * application.authenticateOauthRequest(httpRequest)}
 *   <b>{@link #inLocation(RequestLocation...) .inLocation(RequestLocation.HEADER, RequestLocation.BODY)}
 *   .execute()</b>;
 * </pre>
 *
 * @see com.stormpath.sdk.application.Application#authenticateOauthRequest(Object) application.authenticateOauthRequest(httpRequest)
 * @see #execute()
 * @since 1.0.RC
 */
public interface ResourceRequestAuthenticator {

    /**
     * Specifies the location(s) where the OAuth bearer {@code access_token} will be checked.  Once found, the token
     * will be used to authenticate the OAuth request.  Unless configured via this method, the default locations that
     * will be checked are the {@link RequestLocation#HEADER} and {@link RequestLocation#BODY}.  See
     * {@link RequestLocation} for more information.
     *
     * @param locations the location(s) where the OAuth bearer {@code access_token} will be checked.
     * @return this instance for method chaining.
     * @see RequestLocation
     */
    ResourceRequestAuthenticator inLocation(RequestLocation... locations);

    /**
     * Authenticates the OAuth request using a bearer Access Token and returns a corresponding result.  The result
     * type may be either a {@link OauthAuthenticationResult} or a {@link AccessTokenResult}.  See
     * {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
     * application.authenticateOauthRequest(httpRequest)} for more information.
     *
     * @return the result of the authentication attempt.
     * @deprecated this method will be removed soon. Use {@link ResourceRequestAuthenticator#authenticate(Object)} instead
     */
    OauthAuthenticationResult execute();

    /**
     * Authenticates an OAuth-based HTTP request using a bearer Access Token and returns the corresponding result.
     * The result type may be either a {@link OauthAuthenticationResult} or a {@link AccessTokenResult}.
     * Throws a {@link com.stormpath.sdk.resource.ResourceException} if the request cannot be authenticated.
     *
     * @param httpRequest either a <a href="http://docs.oracle.com/javaee/7/api/javax/servlet/ServletRequest.html">
     *                    {@code javax.servlet.http.HttpServletRequest}</a> instance (if your app runs in a
     *                    Servlet container) or a manually-constructed {@link com.stormpath.sdk.http.HttpRequest}
     *                    instance if it does not.  An argument not of either type will throw an IllegalArgumentException.
     * @return the result of the authentication attempt.
     */
    OauthAuthenticationResult authenticate(HttpRequest httpRequest);

}

