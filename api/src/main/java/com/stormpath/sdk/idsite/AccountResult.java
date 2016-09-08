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
package com.stormpath.sdk.idsite;

import com.stormpath.sdk.account.Account;

/**
 * Represents the result of an ID Site callback redirect, obtained after the end-user has used the ID Site and has been
 * returned to an application's {@code callbackUri}.
 *
 * @see com.stormpath.sdk.application.Application#newIdSiteCallbackHandler(Object)
 * @see com.stormpath.sdk.application.Application#newIdSiteUrlBuilder()
 * @since 1.0.RC2
 */
public interface AccountResult {

    /**
     * Returns the user account that either logged in or was created as a result of registration on the ID Site.  You
     * can determine if the account is newly registered if {@link #isNewAccount() newAccount} is {@code true}.  If
     * {@code newAccount} is false, the account reflects a previously-registered user that has logged in.
     *
     * @return the {@link com.stormpath.sdk.account.Account} Resource containing a user in Stormpath.
     */
    Account getAccount();

    /**
     * Returns {@code true} if the returned {@link #getAccount() account} was newly created (registered) on the ID
     * Site, or {@code false} if the account was an existing account that logged in successfully.
     *
     * @return {@code true} if the returned {@link #getAccount() account} was newly created (registered) on the ID
     *         Site, or {@code false} if the account was an existing account that logged in successfully.
     */
    boolean isNewAccount();

    /**
     * Returns any original application-specific state that was applied when the user was redirected to the ID Site, or
     * {@code null} if no state was specified.  See {@link IdSiteUrlBuilder} and
     * {@link IdSiteUrlBuilder#setState(String)} for more information.
     *
     * @return any original application-specific state that was applied when the user was redirected to the ID Site, or
     *         {@code null} if no state was specified.
     * @see com.stormpath.sdk.application.Application#newIdSiteUrlBuilder()
     * @see IdSiteUrlBuilder
     * @see IdSiteUrlBuilder#setState(String)
     */
    String getState();

    IdSiteResultStatus getStatus();

    void setStatus(IdSiteResultStatus status);
}
