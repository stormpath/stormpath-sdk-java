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
package com.stormpath.sdk.application;

import com.stormpath.sdk.accountStoreMapping.AccountStoreMapping;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;

/**
 * An {@code ApplicationAccountStoreMapping} represents the assignment of an {@link AccountStore AccountStore} (either a {@link Group Group} or
 * {@link Directory Directory}) to an {@link Application Application}.
 * <p/>
 * When an {@code ApplicationAccountStoreMapping} is created, the accounts in the account store are granted access to (become users
 * of) the linked {@code Application}.  The {@link #getListIndex() order} in which {@code AccountStore}s are assigned
 * to an application determines <a href="http://docs.stormpath.com/rest/product-guide/#account-store-mappings">how
 * login attempts work in Stormpath</a>.
 * <h2>Default Account Store</h2>
 * Additionally, an {@code ApplicationAccountStoreMapping} may be designated as the {@link #isDefaultAccountStore() defaultAccountStore}.
 * This causes any accounts created directly by the application to be dispatched to and saved in the associated {@code AccountStore},
 * since Applications cannot store accounts directly.
 * <h2>Default Group Store</h2>
 * Similarly, an {@code ApplicationAccountStoreMapping} may be designated as the {@link #isDefaultGroupStore() defaultGroupStore}.
 * This causes any groups created directly by the application to be dispatched to and saved in the associated {@code AccountStore},
 * since an Application cannot store groups directly.
 * <b>Note:</b> A Group cannot store other Groups.  Therefore, the default group store must be a {@code Directory}.
 *
 * @see com.stormpath.sdk.application.Application#createAccountStoreMapping(ApplicationAccountStoreMapping)
 */
public interface ApplicationAccountStoreMapping extends AccountStoreMapping {

    /**
     * Returns the Application represented by this {@code ApplicationAccountStoreMapping} resource.
     *
     * @return the Application represented by this {@code ApplicationAccountStoreMapping} resource.
     */
    Application getApplication();

    /**
     * Sets the Application represented by this {@code ApplicationAccountStoreMapping} resource.
     *
     * @param application the Application represented by this {@code ApplicationAccountStoreMapping} resource.
     * @return this instance for method chaining.
     */
    ApplicationAccountStoreMapping setApplication(Application application);
}
