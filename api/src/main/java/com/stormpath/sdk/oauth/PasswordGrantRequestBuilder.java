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

import com.stormpath.sdk.directory.AccountStore;

/**
 * Utility class to build {@link PasswordGrantRequestBuilder PasswordGrantRequestBuilder} instances.
 *
 * @since 1.0.RC6
 */
public interface PasswordGrantRequestBuilder extends Oauth2AuthenticationRequestBuilder<PasswordGrantRequest> {

    /**
     * Specifies the login that will be used to create the authentication token.
     *
     * @param login the login that will be used to create the authentication token.
     * @return this instance for method chaining.
     */
    PasswordGrantRequestBuilder setLogin(String login);

    /**
     * Specifies the password that will be used to create the authentication token.
     *
     * @param password the account's raw password.
     * @return this instance for method chaining.
     */
    PasswordGrantRequestBuilder setPassword(String password);

    /**
     * Specifies the target Account Store to be used for the authentication token creation.
     *
     * @param accountStore the sole specific {@link com.stormpath.sdk.directory.AccountStore accountStore} where the provided credentials will be sought in order to authenticate this request.
     * @return this instance for method chaining.
     */
    PasswordGrantRequestBuilder setAccountStore(AccountStore accountStore);
}
