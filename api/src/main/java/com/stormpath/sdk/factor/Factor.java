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
package com.stormpath.sdk.factor;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * A factor represents an additional step in authenticating a resource in the realm of
 * Multi Factor Authentication.
 *
 * @since 1.1.0
 */
public interface Factor extends Resource, Saveable, Deletable, Auditable {

    /**
     * Returns the {@link FactorStatus Factors's status}.
     *
     * @return the {@link FactorStatus Factors's status}.
     */
    FactorStatus getStatus();

    /**
     * Sets the Factor's status. {@link FactorStatus}
     *
     * @param status the Factor's status.
     * @return this instance for method chaining.
     */
    Factor setStatus(FactorStatus status);

    /**
     * Returns the {@link FactorVerificationStatus Factors's verification status}.
     *
     * @return the the {@link FactorVerificationStatus Factors's verification status}.
     */
    FactorVerificationStatus getFactorVerificationStatus();

    /**
     * Sets the {@link FactorVerificationStatus Factors's verification status}.
     *
     * @param verificationStatus the Factor's verification status.
     * @return this instance for method chaining.
     */
    Factor setFactorVerificationStatus(FactorVerificationStatus verificationStatus);

    /**
     * Returns the {@link FactorType type} of the concrete factor instance implementing this interface.
     * <p>
     * There could be multiple factors by which an authentication is challenged: SMS, Google Authenticator, Email, to name a few.
     *
     * @return the Factors's type
     * @see com.stormpath.sdk.factor.sms.SmsFactor
     */
    FactorType getType();

    /**
     * Returns the {@link Account} to which this Factor is associated.
     *
     * @return the {@link Account} to which this Factor is associated.
     */
    Account getAccount();

    /**
     * Sets the the {@link Account} associated with this Factor.
     *
     * @param account associated with this Factor.
     * @return this instance for method chaining.
     */
    Factor setAccount(Account account);
}
