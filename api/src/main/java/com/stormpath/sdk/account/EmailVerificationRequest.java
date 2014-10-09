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
 * The EmailVerificationRequest is used in scenarios where the <a href="http://docs.stormpath.com/console/product-guide/#workflow-automations">
 * Account Registration and Verification workflow</a> is enabled. If the welcome email has not been received by
 * a newly registered account, then the user will not be able to login until the account is verified with the received token.
 * <p/>
 * After providing the username or email identifying the desired account, a call to {@link
 * com.stormpath.sdk.application.Application#sendEmailVerificationToken(EmailVerificationRequest) Application#sendEmailVerificationToken(EmailVerificationRequest)}
 * will trigger the delivery of a new verification token for the specified account.
 * <p/>
 * Although the {@link AccountStore} property is optional it should also be provided when the account's AccountStore is already
 * known since it will speed-up the backend execution time.
 *
 * @see {@link com.stormpath.sdk.application.Application#sendEmailVerificationToken(EmailVerificationRequest)}
 * @since 1.0.0
 */
public interface EmailVerificationRequest extends Resource {

    /**
     * Returns the username or email set.
     *
     * @return The provided username or email.
     */
    String getLogin();

    /**
     * Setter for the account login information: username or email.
     *
     * @param usernameOrEmail the username or email identifying the account that will receive the verification email.
     * @return this instance for method chaining.
     * @since 1.0.0
     */
    EmailVerificationRequest setLogin(String usernameOrEmail);

    /**
     * Returns the {@link AccountStore} set.
     *
     * @return The provided AccountStore.
     */
    AccountStore getAccountStore();

    /**
     * Setter for the {@link AccountStore} where the specified account must be searched. Although this is an optional
     * property it should to be provided when the account's AccountStore is already known since it will speed-up the
     * backend execution time.
     *
     * @param accountStore the {@link AccountStore} where the account must be searched by the backend.
     * @return this instance for method chaining.
     * @since 1.0.0
     */
    EmailVerificationRequest setAccountStore(AccountStore accountStore);

}
