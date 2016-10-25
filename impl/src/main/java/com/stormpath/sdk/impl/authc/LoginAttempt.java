/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.resource.Resource;

/**
 * @since 0.2
 */
public interface LoginAttempt extends Resource {

    String getType();

    void setType(String type);

    /**
     * Sets the `AccountStore` where the login attempt will be targeted to, bypassing the standard
     * cycle-through-all-app-account-stores.
     * <p>
     * Setting the accountStore can't be used along side setting the Organization nameKey, setting the account store would override the Organization nameKey
     *
     * @param accountStore the specific `AccountStore` where the authentication request will be targeted.
     * @since 1.0.alpha
     */
    void setAccountStore(AccountStore accountStore);

    /**
     * Returns the specific account store this authentication request will be targeted to.
     * <p>
     * This would return null is an organization nameKey is set using {@link LoginAttempt#setOrganizationNameKey(String)}
     *
     * @return the specific account store this authentication request will be targeted to.
     * @deprecated since 1.2.0 and will be removed in 2.0.0
     * @since 1.0.alpha
     */
    @Deprecated
    AccountStore getAccountStore();

    /**
     * Sets the an organization name key as the reference account store
     * <p>
     * see: https://github.com/stormpath/stormpath-sdk-java/issues/284
     *
     * @param nameKey an organization name key
     * @since 1.2.0
     */
    void setOrganizationNameKey(String nameKey);
}
