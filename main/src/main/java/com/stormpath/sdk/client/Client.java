/*
 * Copyright 2012 Stormpath, Inc.
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
import com.stormpath.sdk.ds.impl.DefaultDataStore;
import com.stormpath.sdk.http.RequestExecutor;
import com.stormpath.sdk.http.impl.HttpClientRequestExecutor;
import com.stormpath.sdk.tenant.Tenant;

/**
 * @since 0.1
 */
public class Client {

    private DataStore dataStore;

    public Client(ApiKey apiKey) {
        this(apiKey, DefaultDataStore.DEFAULT_API_VERSION);
    }

    public Client(ApiKey apiKey, int apiVersion) {
        RequestExecutor requestExecutor = new HttpClientRequestExecutor(apiKey);
        this.dataStore = new DefaultDataStore(requestExecutor, apiVersion);
    }

    public Client(ApiKey apiKey, String baseUrl) {
        RequestExecutor requestExecutor = new HttpClientRequestExecutor(apiKey);
        this.dataStore = new DefaultDataStore(requestExecutor, baseUrl);
    }

    public Tenant getCurrentTenant() {
        return this.dataStore.load("/tenants/current", Tenant.class);
    }

    public DataStore getDataStore() {
        return this.dataStore;
    }
}
