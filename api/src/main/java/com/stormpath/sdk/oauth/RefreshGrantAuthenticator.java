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

/**
 * This class is used to refresh an OAuth 2.0 token created in Stormpath. For example:
 * <pre>
 * Application app = obtainApplication();
 * RefreshGrantRequest request = Authenticators.REFRESH_GRANT_AUTHENTICATOR.builder()
 *      .setRefreshToken(refreshToken)
 *      .build();
 * OauthGrantAuthenticationResult result = app.authenticate(request)
 * </pre>
 *
 * @see PasswordGrantAuthenticator
 *
 * @since 1.0.RC5.1
 */
public interface RefreshGrantAuthenticator {

    /**
     * Method used to get refreshed Oauth 2.0 Access Tokens
     * @param refreshGrantRequest the {@link RefreshGrantRequest RefreshGrantRequest} instance containing the information required for the request.
     *
     * @return an {@link OauthGrantAuthenticationResult OauthGrantAuthenticationResult} instance containing the resultant {@link AccessToken AccessToken}.
     */
    OauthGrantAuthenticationResult authenticate(RefreshGrantRequest refreshGrantRequest);
}
