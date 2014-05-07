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
package com.stormpath.sdk.account;

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.resource.Resource;

/**
 * @since 0.2
 */
public interface PasswordResetToken extends Resource {

    String getEmail();

    void setEmail(String email);

    Account getAccount();

    /**
     * Setter for the new password that will be instantly applied if the reset token is correctly validated.
     *
     * @param password the new password that will be applied if the reset token is correctly validated.
     * @since 1.0.beta
     */
    PasswordResetToken setPassword(String password);

    /**
     * Sets the `AccountStore` where the reset attempt will be targeted to, bypassing the standard
     * cycle-through-all-app-account-stores.
     *
     * @param accountStore the specific `AccountStore` where the reset request will be targeted to.
     * @since 1.0.beta
     */
    PasswordResetToken setAccountStore(AccountStore accountStore);


}
