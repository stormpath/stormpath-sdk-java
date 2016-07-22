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

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.resource.Resource;

/**
 * This class is used to wrap the information required to build a Grant Authentication request, required to exchange the user's credentials for an OAuth Token.
 *
 * @since 1.0.RC7
 */
public interface OAuthPasswordGrantAuthenticationAttempt extends Resource {

    /**
     * Method used to set the plain password to be used in the token exchange request.
     * @param password the plain password to be used in the exchange request.
     */
    void setPassword(String password);

    /**
     * Method used to set the username to be used in the token exchange request.
     * @param username the username to be used in the token exchange request.
     */
    void setLogin(String username);

    /**
     * Method used to set the {@link AccountStore AccountStore} object that will be used for the token exchange request.
     * @param accountStore the {@link AccountStore AccountStore} object that will be used for the token exchange request.
     */
    void setAccountStore(AccountStore accountStore);

    /**
     * Method used to set the Authentication Grant Type that will be used for the token exchange request. Currently only "password" grant type is supported for this operation.
     * @param grantType the Authentication Grant Type that will be used for the token exchange request.
     */
    void setGrantType(String grantType);
}
