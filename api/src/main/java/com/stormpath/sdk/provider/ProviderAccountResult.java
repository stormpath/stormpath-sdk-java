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
package com.stormpath.sdk.provider;

import com.stormpath.sdk.account.Account;

/**
 * Represents the result of the attempt to access the Provider's account. Stormpath maps common fields of the
 * Provider user to the {@link Account} Resource.
 * <p/>
 * If the user retrieved from the Provider did not previously exist in Stormapth as an Account, common Provider user fields
 * will be used to create a new {@link Account} in Stormpath.
 *
 * @see com.stormpath.sdk.application.Application#getAccount(ProviderAccountRequest)
 * @since 1.0.beta
 */
public interface ProviderAccountResult {

    /**
     * Getter for the {@link Account} Resource containing common fields of the Provider user in Stormpath.
     *
     * @return the {@link Account} Resource containing common fields of the Provider user in Stormpath.
     */
    Account getAccount();

    /**
     * If this request generated a new Account in Stormpath, this method will return `true`.
     *
     * @return <code>true</code> if a new {@link Account} was generated in Stormpath as result of the request; <code>false</code> otherwise.
     */
    boolean isNewAccount();

}
