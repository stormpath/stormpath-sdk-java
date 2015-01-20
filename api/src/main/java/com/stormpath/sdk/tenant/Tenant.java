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
package com.stormpath.sdk.tenant;

import com.stormpath.sdk.resource.Extendable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * A {@code Tenant} represents a customer's private data 'space' within Stormpath that contains all of the customer's
 * Stormpath-stored resources, like {@link com.stormpath.sdk.application.Application Applications},
 * {@link com.stormpath.sdk.directory.Directory Directories}, {@link com.stormpath.sdk.account.Account Accounts} and
 * {@link com.stormpath.sdk.group.Group Groups}.
 * <h3>TenantActions</h3>
 * <p>
 * Additionally, while this {@code Tenant} interface represents the actual Tenant resource, many tenant
 * behaviors/actions are defined in the parent {@link TenantActions} interface.  {@link TenantActions} is
 * implemented by both the Tenant implementation <em>and</em> the {@link com.stormpath.sdk.client.Client Client}.
 * This is a convenience where common tenant-wide actions may be performed directly via the Client instance.  For
 * example:
 * <pre>
 * ApplicationList myApplications = client.getApplications();
 * </pre>
 * instead of forcing you to make an intermediate {@code getCurrentTenant()} call:
 * <pre>
 * ApplicationList myApplications = client.getCurrentTenant().getApplications();
 * </pre>
 * which can sometimes make for more readable code.
 * </p>
 * <p>If you can't find what you're looking for in this interface, be sure to check {@link TenantActions} and it will
 * likely be there.</p>
 *
 * @see TenantActions
 *
 * @since 0.1
 */
public interface Tenant extends Resource, Saveable, TenantActions, Extendable {

    /**
     * <p>Returns the tenant's globally-unique name in Stormpath.</p>
     * <p>
     * <b>THIS CAN CHANGE IN THE FUTURE.  Do not rely on it as a permanent identifier.</b>  If you need a permanent ID,
     * use the {@link #getHref() href} as the permanent ID (this is true for all resources, not just Tenant resources).
     * </p>
     *
     * @return the tenant's Stormpath globally-unique name. THIS CAN CHANGE. Do not rely on it as a permanent
     *         identifier.
     */
    String getName();

    /**
     * <p>Returns the tenant's globally-unique human-readable key in Stormpath.</p>
     *
     * <p><b>THIS CAN CHANGE IN THE FUTURE.  Do not rely on it as a permanent identifier.</b>  If you need a permanent ID,
     * use the {@link #getHref() href} as the permanent ID (this is true for all resources, not just Tenant resources).
     * </p>
     *
     * @return the tenant's Stormpath globally-unique human-readable name key. THIS CAN CHANGE. Do not rely on it as a
     *         permanent identifier.
     */
    String getKey();

}
