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

import com.stormpath.sdk.api.ApiKey;

/**
 * ApiAuthenticationResult represents an {@code Api} {@link AuthenticationResult authentication result}.
 *
 * @see com.stormpath.sdk.authc.AuthenticationResultVisitor
 * @see com.stormpath.sdk.oauth.authc.OauthAuthenticationResult
 * @see com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationResult
 * @since 1.0.RC
 */
public interface ApiAuthenticationResult extends AuthenticationResult {

    /**
     * Returns the {@link ApiKey} of this {@link AuthenticationResult}.
     *
     * @return - The {@link ApiKey} of this {@link AuthenticationResult}.
     */
    ApiKey getApiKey();

}
