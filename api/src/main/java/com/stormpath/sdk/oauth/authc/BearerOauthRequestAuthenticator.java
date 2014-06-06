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

/**
 * An OAuth-specific {@code ApiRequestAuthenticator} that authenticates an API Request based on the presence of an OAuth
 * bearer Access Token.  This interface reflects the
 * <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> to allow customization of how
 * the authentication attempt is processed.  For example:
 * <p>
 * <pre>
 * AuthenticationResult result = {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * application.authenticateOauthRequest(httpRequest)}
 *   <b>{@link #inLocation(RequestLocation...) .inLocation(RequestLocation.HEADER, RequestLocation.BODY)}
 *   .execute()</b>;
 * </pre>
 * </p>
 *
 * @see com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * @see #execute()
 * @since 1.0.RC
 */
public interface BearerOauthRequestAuthenticator {

    /**
     * Specifies the location(s) where the OAuth bearer {@code access_token} will be checked.  Once found, the token
     * will be used to authenticate the OAuth request.
     *
     * @param locations the location(s) where the OAuth bearer {@code access_token} will be checked.
     * @return this instance for method chaining.
     */
    BearerOauthRequestAuthenticator inLocation(RequestLocation... locations);

    /**
     * Authenticates the OAuth request using a bearer Access Token.
     *
     * @return the result of the authentication attempt.
     */
    OauthAuthenticationResult execute();

}

