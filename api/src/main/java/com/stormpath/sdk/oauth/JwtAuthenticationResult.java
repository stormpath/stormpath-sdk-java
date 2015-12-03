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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;

/**
 * This class stores the information received after a {@link AccessToken AccessToken} authentication request.
 *
 * @since 1.0.RC7
 */
public interface JwtAuthenticationResult extends Oauth2AuthenticationResult {

    /**
     * Returns the {@link com.stormpath.sdk.account.Account Account} associated to the authenticated {@link AccessToken AccessToken}
     * @return the {@link com.stormpath.sdk.account.Account Account} associated to the authenticated {@link AccessToken AccessToken}
     */
    Account getAccount();

    /**
     * Returns the {@link com.stormpath.sdk.application.Application Application} associated to the authenticated {@link AccessToken AccessToken}
     * @return the {@link com.stormpath.sdk.application.Application Application} associated to the authenticated {@link AccessToken AccessToken}
     */
    Application getApplication();

    /**
     * Returns the href of the authenticated {@link AccessToken AccessToken}
     * @return the href the authenticated {@link AccessToken AccessToken}
     */
    String getHref();

    /**
     * Returns the String that corresponds to JWT of the authenticated {@link AccessToken AccessToken}
     * @return the String that corresponds to JWT of the authenticated {@link AccessToken AccessToken}
     */
    String getJwt();
}
