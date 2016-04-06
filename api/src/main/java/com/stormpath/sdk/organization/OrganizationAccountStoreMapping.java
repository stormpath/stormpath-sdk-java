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
package com.stormpath.sdk.organization;

import com.stormpath.sdk.accountStoreMapping.AccountStoreMapping;
import com.stormpath.sdk.directory.AccountStore;

/**
 * An {@code OrganizationAccountStoreMapping} represents the assignment of an {@link AccountStore AccountStore} (either a {@link com.stormpath.sdk.group.Group Group} or
 * {@link com.stormpath.sdk.directory.Directory Directory}) to a {@link Organization Organization}.
 * <p/>
 * When an {@code OrganizationAccountStoreMapping} is created, the accounts in the account store are granted access to (become users
 * of) the linked {@code Organization}.  The {@link #getListIndex() order} in which {@code AccountStore}s are assigned
 * to an organization determines <a href="http://docs.stormpath.com/rest/product-guide/#account-store-mappings">how
 * login attempts work in Stormpath</a>.
 * <h2>Default Account Store</h2>
 * Additionally, an {@code OrganizationAccountStoreMapping} may be designated as the {@link #isDefaultAccountStore() defaultAccountStore}.
 * This causes any accounts created directly by the organization to be dispatched to and saved in the associated {@code AccountStore}, 
 * since Organizations cannot store accounts directly.
 * <h2>Default Group Store</h2>
 * Similarly, an {@code OrganizationAccountStoreMapping} may be designated as the {@link #isDefaultGroupStore() defaultGroupStore}.
 * This causes any groups created directly by the organization to be dispatched to and saved in the associated {@code AccountStore}, 
 * since an Organization cannot store groups directly.
 * <b>Note:</b> A Group cannot store other Groups.  Therefore, the default group store must be a {@code Directory}.
 *
 * @see com.stormpath.sdk.organization.Organization#createAccountStoreMapping(OrganizationAccountStoreMapping)
 *
 * @since 1.0.RC7
 */
public interface OrganizationAccountStoreMapping extends AccountStoreMapping {

    /**
     * Returns the Organization represented by this {@code OrganizationAccountStoreMapping} resource.
     *
     * @return the Organization represented by this {@code OrganizationAccountStoreMapping} resource.
     */
    Organization getOrganization();

    /**
     * Sets the Organization represented by this {@code OrganizationAccountStoreMapping} resource.
     *
     * @param organization the Organization represented by this {@code OrganizationAccountStoreMapping} resource.
     * @return this instance for method chaining.
     */
    OrganizationAccountStoreMapping setOrganization(Organization organization);
}
