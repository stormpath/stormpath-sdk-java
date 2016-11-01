/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.application.webconfig;

import com.stormpath.sdk.application.Application;

/**
 * VerifyEmailConfig exposes the configurable properties for email verification when creating an account through the
 * Stormpath Client Api.
 *
 * @since 1.2.0
 */
public interface VerifyEmailConfig extends WebFeatureConfig<VerifyEmailConfig> {

    /**
     * Returns whether the email verification should be enabled or disabled when an account is created through the
     * Stormpath Client Api. If the return value is {@code null} it means that an email will be verified only if the
     * <p/>
     * <list>
     * <ul>{@link Boolean#TRUE} - The account created will require email verification.</ul>
     * <ul> {@link Boolean#FALSE} - The account created won't require email verification</ul>
     * <ul>{@code null} - Email Verification will be enforced  by {@link Application#getDefaultAccountStore() defaultAccountStore} of the
     * application. </ul>
     * <p/>
     * </list>
     *
     * @return whether the email verification should be enabled or disabled.
     */
    Boolean isEnabled();

    /**
     * Sets whether the email verification should be enabled or disabled when an account is created through the
     * Stormpath Client Api.
     *
     * @param enabled Boolean value to enable or disable email verifications for accounts.
     */
    VerifyEmailConfig setEnabled(Boolean enabled);

}
