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
package com.stormpath.sdk.oauth;

import com.stormpath.sdk.lang.Classes;

/**
 * Static utility/helper methods serving Grant Authentication Request Builders. For example, to
 * construct a builder for a Create Grant Authentication request:
 * <pre>
 * PasswordGrantRequest request = Authenticators.PASSWORD_GRANT_AUTHENTICATOR.builder()
 *      .setLogin(username)
 *      .setPassword(password)
 *      .build();
 * </pre>
 * or, to construct a builder for a Refresh Grant Authentication request:
 * <pre>
 * RefreshGrantRequest request = Authenticators.REFRESH_GRANT_AUTHENTICATOR.builder()
 *      .setRefreshToken(refreshToken)
 *      .build();
 * </pre>
 *
 * @since 1.0.RC5.1
 */
public final class Authenticators {

    private Authenticators() {
    }

    /**
     * Returns a new {@link PasswordGrantRequestFactory} instance, used to construct Create Grant Authentication requests.
     *
     * @return a new {@link PasswordGrantRequestFactory} instance, used to construct Create Grant Authentication requests.
     */
    public static final PasswordGrantRequestFactory PASSWORD_GRANT_AUTHENTICATOR = (PasswordGrantRequestFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultPasswordGrantRequestFactory");

    /**
     * Returns a new {@link RefreshGrantRequestFactory} instance, used to construct Refresh Grant Authentication requests.
     *
     * @return a new {@link RefreshGrantRequestFactory} instance, used to construct Refresh Grant Authentication requests.
     */
    public static final RefreshGrantRequestFactory REFRESH_GRANT_AUTHENTICATOR = (RefreshGrantRequestFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultRefreshGrantRequestFactory");
}
