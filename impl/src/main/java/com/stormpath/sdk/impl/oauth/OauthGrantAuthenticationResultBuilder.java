/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.oauth.OauthGrantAuthenticationResult;

/**
 * This builder is used to obtain an {@link com.stormpath.sdk.oauth.OauthGrantAuthenticationResult OauthGrantAuthenticationResult} object from the result obtained after a Grant Authentication operation is performed.
 *
 * @since 1.0.RC6
 */
public interface OauthGrantAuthenticationResultBuilder {

    /**
     * Modifies this builder to state whether the result to build corresponds to a Refresh Grant Authentication operation
     *
     * @param isRefreshAuthGrantRequest {@code true} if the result to build corresponds to a Refresh Grant Authentication operation,
     * {@code false} if it corresponds to a Create Grant Authentication operation
     *
     * @return the {@link OauthGrantAuthenticationResultBuilder OauthGrantAuthenticationResultBuilder} object
     */
    OauthGrantAuthenticationResultBuilder setIsRefreshAuthGrantRequest(Boolean isRefreshAuthGrantRequest);

    /**
     * Creates a new {@code OauthGrantAuthenticationResult} instance based on the current builder state.
     *
     * @return a new {@code OauthGrantAuthenticationResult} instance based on the current builder state.
     */
    OauthGrantAuthenticationResult build();
}
