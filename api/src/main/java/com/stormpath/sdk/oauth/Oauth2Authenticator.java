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
 * Interface defining the operations that every Oauth2 Authenticator must support.
 *
 * @param <T> the kind of {@link Oauth2AuthenticationResult} that this Oauth2 Authenticator will return after successful authentication.
 *
 * @see com.stormpath.sdk.oauth.PasswordGrantAuthenticator
 * @see com.stormpath.sdk.oauth.RefreshGrantAuthenticator
 * @see com.stormpath.sdk.oauth.JwtAuthenticator
 *
 * @since 1.0.RC7
 */
public interface Oauth2Authenticator<T extends Oauth2AuthenticationResult> {

    /**
     * An attempt to perform any kind of Oauth2 Authentication: {@link com.stormpath.sdk.oauth.PasswordGrantAuthenticator Password Grant},
     * {@link com.stormpath.sdk.oauth.RefreshGrantAuthenticator Refresh Grant} and {@link com.stormpath.sdk.oauth.JwtAuthenticator JWT}.
     *
     * @param authenticationRequest the {@link Oauth2AuthenticationRequest} that this authenticator will attempt.
     * @return a sub-class of {@link Oauth2AuthenticationResult} providing all the properties associated with a successful authentication.
     */
    T authenticate(Oauth2AuthenticationRequest authenticationRequest);

}
