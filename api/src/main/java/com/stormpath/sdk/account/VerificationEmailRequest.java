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

/**
 * The VerificationEmailRequest is used in scenarios where the <a href="http://docs.stormpath.com/console/product-guide/#workflow-automations">
 * Account Registration and Verification workflow</a> is enabled. If the welcome email has not been received by
 * a newly registered account, then the user will not be able to login until the account is verified with the received email.
 * <p/>
 * After providing the username or email identifying the desired account, a call to {@link
 * com.stormpath.sdk.application.Application#sendVerificationEmail(VerificationEmailRequest) Application#sendVerificationEmail(VerificationEmailRequest)}
 * will trigger the delivery of a new verification token for the specified account.
 * <p/>
 * Although the {@link AccountStore} property is optional it should also be provided when the account's AccountStore is already
 * known since it will speed-up the backend execution time.
 *
 * @see {@link com.stormpath.sdk.application.Application#sendVerificationEmail(VerificationEmailRequest)}
 * @since 1.0.0
 */
public interface VerificationEmailRequest {

    /**
     * Returns the username or email.
     *
     * @return the username or email.
     */
    String getLogin();

    /**
     * Returns the organizationNameKey.
     *
     * @return the organizationNameKey.
     */
    String getOrganizationNameKey();

    /**
     * Returns the {@link AccountStore} set.
     *
     * @return The provided AccountStore.
     */
    AccountStore getAccountStore();

}
