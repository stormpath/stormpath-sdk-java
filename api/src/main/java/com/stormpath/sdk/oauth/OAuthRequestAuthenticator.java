/*
 * Copyright 2015 Stormpath, Inc.
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

/**
 * Interface defining the operations that every OAuth2 Authenticator must support.
 *
 * @param <T> the kind of {@link OAuthRequestAuthenticationResult} that this OAuth2 Authenticator will return after successful authentication.
 *
 * @see OAuthPasswordGrantRequestAuthenticator
 * @see OAuthRefreshTokenRequestAuthenticator
 * @see OAuthBearerRequestAuthenticator
 *
 * @since 1.0.RC7
 */
public interface OAuthRequestAuthenticator<T extends OAuthRequestAuthenticationResult> {

    /**
     * An attempt to perform any kind of OAuth2 Authentication: {@link OAuthPasswordGrantRequestAuthenticator Password Grant},
     * {@link OAuthRefreshTokenRequestAuthenticator Refresh Grant} and {@link OAuthBearerRequestAuthenticator JWT}.
     *
     * @param authenticationRequest the {@link OAuthRequestAuthentication} that this authenticator will attempt.
     * @return a sub-class of {@link OAuthRequestAuthenticationResult} providing all the properties associated with a successful authentication.
     */
    T authenticate(OAuthRequestAuthentication authenticationRequest);

}
