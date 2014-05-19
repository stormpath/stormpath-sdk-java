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

/**
 * The {@code Client} is the main entry point to the Stormpath Java SDK.  A Java project wishing to
 * communicate with the Stormpath REST API service must build a {@code Client} instance.  After obtaining
 * a {@code Client instance}, the REST API may be used by making simple Java calls on objects returned from
 * the Client (or any children objects obtained therein).
 * <p/>
 * For example:
 * <pre>
 * String path = System.getProperty("user.home") + "/.stormpath/<a href="http://www.stormpath.com/docs/get-api-key">apiKey.properties</a>";
 * Client client = {@link Clients Clients}.builder()
 *      .setApiKey(ApiKeys.builder()
 *          .setFileLocation(path)
 *          .build()
 *      )
 *      .build();
 *
 * //interact with the REST API resources as desired:
 * Tenant myTenant = client.getCurrentTenant();
 *
 * ApplicationList applications = tenant.getApplications();
 *
 * System.out.println("My Applications: ");
 * for (Application application : applications) {
 *     System.out.println(application);
 * }
 * </pre>
 * <h3>DataStore API</h3>
 * As of 0.8, this interface extends {@link DataStore}, but the actual class implementation will merely act as a
 * wrapper to its internal 'real' {@code DataStore} instance.  This is a convenience mechanism to eliminate the constant need to
 * call {@code client.getDataStore()} every time one needs to instantiate or look up a Resource.
 *
 * @see <a href="http://www.stormpath.com/docs/quickstart/connect">Communicating with Stormpath: Get your API Key</a>
 * @since 0.1
 */
public interface Client extends DataStore, TenantActions {

    /**
     * Returns the sole {@link Tenant} resource associated to this client.
     * @return the sole {@link Tenant} resource associated to this client.
     */
    Tenant getCurrentTenant();

    /**
     * Returns the internal {@link DataStore} of the client.
     * @return the internal {@link DataStore} of the client.
     */
    DataStore getDataStore();

}






