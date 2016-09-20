/*
* Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.phone;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * A phone is associated with an {@link Account} to be used for Multi Factor Authentication.
 *
 * @since 1.1.0
 */
public interface Phone extends Resource, Saveable, Deletable, Auditable {

    /**
     * Returns the phone number.
     *
     * @return the phone number.
     */
    String getNumber();

    /**
     * Sets the phone number.
     * @param number the phone number
     *
     * @return this instance for method chaining.
     */
    Phone setNumber(String number);

    /**
     * Returns the phone's name.
     *
     * @return the phone's name.
     */
    String getName();

    /**
     * Sets the phone's name.
     * @param name he phone's name.
     *
     * @return this instance for method chaining.
     */
    Phone setName(String name);

    /**
     * Returns the phone's description.
     *
     * @return the phone's description.
     */
    String getDescription();

    /**
     * Sets the phone's description.
     * @param description he phone's description.
     *
     * @return this instance for method chaining.
     */
    Phone setDescription(String description);

    /**
     * Returns the phone's status.
     * {@link PhoneStatus}
     *
     * @return the phone's status.
     */
    PhoneStatus getStatus();

    /**
     * Sets the phone's status.
     * @param status the phone's status.
     * {@link PhoneStatus}
     *
     * @return this instance for method chaining.
     */
    Phone setStatus(PhoneStatus status);

    /**
     * Returns the phone's verification status.
     * {@link PhoneVerificationStatus}
     *
     * @return the phone's verification status.
     */
    PhoneVerificationStatus getVerificationStatus();

    /**
     * Sets the phone's verification status.
     * @param verificationStatus the phone's verificationStatus.
     * {@link PhoneStatus}
     *
     * @return this instance for method chaining.
     */
    Phone setVerificationStatus(PhoneVerificationStatus verificationStatus);

    /**
     * Returns the {@link Account} associated with this phone.
     *
     * @return the {@link Account} associated with this phone.
     */
    Account getAccount();

    /**
     * Sets the {@link Account} associated with this phone.
     * @param account the {@link Account} associated with this phone.
     *
     * @return this instance for method chaining.
     */
    Phone setAccount(Account account);
}
