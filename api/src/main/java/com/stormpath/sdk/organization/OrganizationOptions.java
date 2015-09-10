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

import com.stormpath.sdk.directory.AccountStoreOptions;
import com.stormpath.sdk.query.Options;

/**
 * Organization-specific options that may be specified when retrieving {@link Organization} resources.
 *
 * @since 1.0.RC4.6
 */
public interface OrganizationOptions<T> extends Options {

    /**
     * Ensures that when retrieving an Organization, the Organization's {@link Organization#getCustomData() customData} is also
     * retrieved in the same request. This enhances performance by leveraging a single request to retrieve multiple
     * related resources you know you will use.
     *
     * @return this instance for method chaining.
     */
    T withCustomData();

    /**
     * Ensures that when retrieving the resource, the owning {@link com.stormpath.sdk.tenant.Tenant Tenant} is also
     * retrieved in the same request.  This enhances performance by leveraging a single request to retrieve multiple
     * related resources you know you will use.
     *
     * @return this instance for method chaining.
     */
    T withTenant();

    /**
     * Ensures that when retrieving the resource, its associated {@link com.stormpath.sdk.organization.OrganizationAccountStoreMapping accountStoreMappings}
     * are also retrieved in the same request (paginated).  This enhances performance by leveraging a single request
     * to retrieve multiple related resources you know you will use.
     * <p/>
     * If you wish to control pagination parameters (offset and limit) for the
     * returned accountStoreMappings, see the {@link #withOrganizationAccountStoreMappings(int) withOrganizationAccountStoreMappings(limit)} or
     * {@link #withOrganizationAccountStoreMappings(int, int) withOrganizationAccountStoreMappings(limit,offset)} methods.
     *
     * @return this instance for method chaining.
     */
    T withOrganizationAccountStoreMappings();

    /**
     * Ensures that when retrieving the resource, its associated {@link OrganizationAccountStoreMapping accountStoreMappings} are also
     * retrieved in the same request (paginated), limiting the first page of OrganizationAccountStoreMapping results to {@code limit} items.
     * This enhances performance by leveraging a single request to retrieve multiple related resources you know you
     * will use.
     *
     * @param limit the number of results in the OrganizationAccountStoreMappings collection's first page.  Min: 1, Max: 100.
     * @return this instance for method chaining.
     */
    T withOrganizationAccountStoreMappings(int limit);

    /**
     * Ensures that when retrieving the resource, its associated {@link com.stormpath.sdk.organization.OrganizationAccountStoreMapping accountStoreMappings} are also
     * retrieved in the same request (paginated) , with the first page of OrganizationAccountStoreMapping results starting at the specified
     * {@code offset} index and limiting the number of results to {@code limit} items.  This enhances performance by
     * leveraging a single request to retrieve multiple related resources you know you will use.
     *
     * @param limit  the number of results in the OrganizationAccountStoreMappings collection's first page.  Min: 1, Max: 100.
     * @param offset the starting index of the first OrganizationAccountStoreMapping to retrieve in the overall OrganizationAccountStoreMappings collection's result set.
     * @return this instance for method chaining.
     */
    T withOrganizationAccountStoreMappings(int limit, int offset);
}
