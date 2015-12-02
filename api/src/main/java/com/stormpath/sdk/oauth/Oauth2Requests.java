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
 * construct a builder to create a {@link PasswordGrantRequest}:
 * <pre>
 * PasswordGrantRequest request = Oauth2Requests.PASSWORD_GRANT_REQUEST.builder()
 *      .setLogin(username)
 *      .setPassword(password)
 *      .build();
 * </pre>
 * or, to construct a builder for a Refresh Grant Authentication request:
 * <pre>
 * RefreshGrantRequest request = Oauth2Requests.REFRESH_GRANT_REQUEST.builder()
 *      .setRefreshToken(refreshToken)
 *      .build();
 * </pre>
 *
 * @since 1.0.RC7
 */
public final class Oauth2Requests {

    private Oauth2Requests() {
    }

    /**
     * Returns a new {@link PasswordGrantAuthenticationRequestFactory} instance, used to construct Create Grant Authentication requests.
     *
     * @return a new {@link PasswordGrantAuthenticationRequestFactory} instance, used to construct Create Grant Authentication requests.
     */
    public static final PasswordGrantAuthenticationRequestFactory PASSWORD_GRANT_REQUEST = (PasswordGrantAuthenticationRequestFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultPasswordGrantAuthenticationRequestFactory");

    /**
     * Returns a new {@link RefreshAuthenticationRequestFactory} instance, used to construct Refresh Grant Authentication requests.
     *
     * @return a new {@link RefreshAuthenticationRequestFactory} instance, used to construct Refresh Grant Authentication requests.
     */
    public static final RefreshAuthenticationRequestFactory REFRESH_GRANT_REQUEST = (RefreshAuthenticationRequestFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultRefreshAuthenticationRequestFactory");

    /**
     * Returns a new {@link JwtAuthenticationRequestFactory} instance, used to authenticate JWT Access Tokens.
     *
     * @return a new {@link JwtAuthenticationRequestFactory} instance, used to authenticate JWT Access Tokens.
     */
    public static final JwtAuthenticationRequestFactory JWT_AUTHENTICATION_REQUEST = (JwtAuthenticationRequestFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultJwtAuthenticationRequestFactory");

    /**
     * Returns a new {@link PasswordGrantAuthenticationRequestFactory} instance, used to construct Create Grant Authentication requests.
     *
     * @return a new {@link PasswordGrantAuthenticationRequestFactory} instance, used to construct Create Grant Authentication requests.
     */
    public static final IdSiteAuthenticationRequestFactory IDSITE_AUTHENTICATION_REQUEST = (IdSiteAuthenticationRequestFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultIdSiteAuthenticationRequestFactory");
}
