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
package com.stormpath.sdk.client;

import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.tenant.Tenant;
import com.stormpath.sdk.tenant.TenantActions;
import com.stormpath.sdk.tenant.TenantOptions;

/**
 * The {@code Client} is the main entry point to the Stormpath Java SDK.  A JVM project wishing to
 * communicate with the Stormpath REST API service must build a {@code Client} instance.  After obtaining
 * a {@code Client instance}, the REST API may be used by making simple Java calls on objects returned from
 * the Client (or any children objects obtained therein).
 * <p/>
 * For example:
 * <pre>
 * String path = System.getProperty("user.home") + "/.stormpath/<a href="http://www.stormpath.com/docs/get-api-key">apiKey.properties</a>";
 * Client client = {@link Clients Clients}.{@link com.stormpath.sdk.client.Clients#builder() builder()}
 *     .{@link com.stormpath.sdk.client.ClientBuilder#setApiKey(ApiKey) setApiKey}({@link ApiKeys ApiKeys}.builder()
 *         .setFileLocation(path)
 *         .build())
 *     .build();
 *
 * ApplicationList applications = client.getApplications();
 *
 * System.out.println("My Applications: ");
 *
 * for (Application application : applications) {
 *     System.out.println(application);
 * }
 * </pre>
 * <h3>DataStore API</h3>
 * As of 0.8, this interface extends {@link DataStore}, but the actual class implementation will merely act as a
 * wrapper to its internal 'real' {@code DataStore} instance.  This is a convenience mechanism to eliminate the constant need to
 * call {@code client.getDataStore()} every time one needs to instantiate or look up a Resource.
 *
 * <h3>TenantActions API</h3>
 * <p>As of 1.0.RC, this interface extends {@link TenantActions} to allow for a more convenient way of performing
 * Tenant behavior when interacting with a {@code Client} instance directly. For example, instead of:
 * <pre>
 * client.getCurrentTenant().getApplications();
 * </pre>
 * one might choose to write:
 * <pre>
 * client.getApplications();
 * </pre>
 * which is less verbose and probably better self-documenting for many use cases.
 * </p>
 * <p>
 * All Client {@code TenantActions} method implementations simply delegate to
 * <pre>
 * {@link #getCurrentTenant() getCurrentTenant()}.<em>methodName</em>
 * </pre>
 * <p/>
 *
 * @see <a href="http://www.stormpath.com/docs/quickstart/connect">Communicating with Stormpath: Get your API Key</a>
 * @see TenantActions
 * @see DataStore
 * @since 0.1
 */
public interface Client extends DataStore, TenantActions {

    /**
     * Returns the sole {@link Tenant} resource associated to this client.
     * @return the sole {@link Tenant} resource associated to this client.
     */
    Tenant getCurrentTenant();

    /**
     * Returns the internal {@link DataStore} of the client.  It is typically not necessary to invoke this method as
     * the Client implements the {@link DataStore} API and will delegate to this instance automatically.
     *
     * @return the client's internal {@link DataStore}.
     */
    DataStore getDataStore();

    /**
     * Returns the {@link Tenant} resource associated to this client, customized by the specified {@link TenantOptions}
     *
     * @param tenantOptions The {@link TenantOptions} to use to customize the retrieved Tenant resource.
     * @return the {@link Tenant} resource customized by the specified {@link TenantOptions}
     *
     * @since 1.0.RC4.3-SNAPSHOT
     */
    Tenant getCurrentTenant(TenantOptions tenantOptions);
}






