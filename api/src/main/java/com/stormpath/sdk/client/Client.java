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
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.tenant.Tenant;

import java.lang.reflect.Constructor;

/**
 * The {@code Client} is the main entry point to the Stormpath Java SDK.  A Java project wishing to
 * communicate with the Stormpath REST API service must instantiate a {@code Client} instance.  After obtaining
 * a {@code Client instance}, the REST API may be used by making simple Java calls on objects returned from
 * the Client (or any children objects obtained therein).
 * <p/>
 * For example:
 * <pre>
 * String path = System.getProperty("user.home") + "/.stormpath/<a href="http://www.stormpath.com/docs/get-api-key">apiKey.properties</a>";
 * Client client = new {@link ClientBuilder ClientBuilder}().setApiKeyFileLocation(path).build();
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
 * As of 0.8, this class implements the {@link DataStore} interface, but the implementation merely acts as a wrapper to
 * the underlying 'real' {@code DataStore} instance.  This is a convenience mechanism to eliminate the constant need to
 * call {@code client.getDataStore()} every time one needs to instantiate or look up a Resource.
 *
 * @since 0.1
 * @see <a href="http://www.stormpath.com/docs/quickstart/connect">Communicating with Stormpath: Get your API Key</a>
 */
public class Client implements DataStore {

    public static final int DEFAULT_API_VERSION = 1;

    private final DataStore dataStore;

    /**
     * Instantiates a new Client instance that will communicate with the Stormpath REST API.  See the class-level
     * JavaDoc for a usage example.
     *
     * @param apiKey the Stormpath account API Key that will be used to authenticate the client with
     *               Stormpath's REST API.
     */
    public Client(ApiKey apiKey) {
        if (apiKey == null) {
            throw new IllegalArgumentException("apiKey argument cannot be null.");
        }
        Object requestExecutor = createRequestExecutor(apiKey, null);
        this.dataStore = createDataStore(requestExecutor, DEFAULT_API_VERSION);
    }

    /**
     * Instantiates a new Client instance that will communicate with the Stormpath REST API using an HTTP Proxy.
     *
     * @param apiKey the Stormpath account API Key that will be used to authenticate the client with
     *               Stormpath's REST API.
     */
    public Client(ApiKey apiKey, Proxy proxy) {
        if (apiKey == null) {
            throw new IllegalArgumentException("apiKey argument cannot be null.");
        }
        if (proxy == null) {
            throw new IllegalArgumentException("proxy argument cannot be null.");
        }
        Object requestExecutor = createRequestExecutor(apiKey, proxy);
        this.dataStore = createDataStore(requestExecutor, DEFAULT_API_VERSION);
    }

    //no modifier on purpose: for local development testing only:
    Client(ApiKey apiKey, String baseUrl) {
        if (apiKey == null) {
            throw new IllegalArgumentException("apiKey argument cannot be null.");
        }
        Object requestExecutor = createRequestExecutor(apiKey, null);
        this.dataStore = createDataStore(requestExecutor, baseUrl);
    }

    //no modifier on purpose: for local development testing only:
    Client(ApiKey apiKey, Proxy proxy, String baseUrl) {
        if (apiKey == null) {
            throw new IllegalArgumentException("apiKey argument cannot be null.");
        }
        if (proxy == null) {
            throw new IllegalArgumentException("proxy argument cannot be null.");
        }
        Object requestExecutor = createRequestExecutor(apiKey, proxy);
        this.dataStore = createDataStore(requestExecutor, baseUrl);
    }

    public Tenant getCurrentTenant() {
        return this.dataStore.getResource("/tenants/current", Tenant.class);
    }

    public DataStore getDataStore() {
        return this.dataStore;
    }

    //since 0.3
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object createRequestExecutor(ApiKey apiKey, Proxy proxy) {

        String className = "com.stormpath.sdk.impl.http.httpclient.HttpClientRequestExecutor";

        Class requestExecutorClass;

        if (Classes.isAvailable(className)) {
            requestExecutorClass = Classes.forName(className);
        } else {
            //we might be able to check for other implementations in the future, but for now, we only support
            //HTTP calls via the HttpClient.  Throw an exception:

            String msg = "Unable to find the '" + className + "' implementation on the classpath.  Please ensure you " +
                    "have added the stormpath-sdk-impl-httpclient .jar file to your runtime classpath.";
            throw new RuntimeException(msg);
        }

        Constructor ctor = Classes.getConstructor(requestExecutorClass, ApiKey.class, Proxy.class);

        return Classes.instantiate(ctor, apiKey, proxy);
    }

    //@since 0.3
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private DataStore createDataStore(Object requestExecutor, Object secondCtorArg) {

        String requestExecutorInterfaceClassName = "com.stormpath.sdk.impl.http.RequestExecutor";
        Class requestExecutorInterfaceClass;

        try {
            requestExecutorInterfaceClass = Classes.forName(requestExecutorInterfaceClassName);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to load required interface: " + requestExecutorInterfaceClassName +
                    ".  Please ensure you have added the stormpath-sdk-impl .jar file to your runtime classpath.", t);
        }

        String className = "com.stormpath.sdk.impl.ds.DefaultDataStore";
        Class dataStoreClass;

        try {
            dataStoreClass = Classes.forName(className);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to load default DataStore implementation class: " +
                    className + ".  Please ensure you have added the stormpath-sdk-impl .jar file to your " +
                    "runtime classpath.", t);
        }

        Class secondCtorArgClass = secondCtorArg.getClass();
        if (Integer.class.equals(secondCtorArgClass)) {
            secondCtorArgClass = int.class;
        }

        Constructor ctor = Classes.getConstructor(dataStoreClass, requestExecutorInterfaceClass, secondCtorArgClass);

        try {
            return (DataStore) ctor.newInstance(requestExecutor, secondCtorArg);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to instantiate DataStore implementation: " + className, t);
        }
    }

    // ========================================================================
    // DataStore methods (delegate to underlying DataStore instance)
    // ========================================================================

    /**
     * Delegates to the internal {@code dataStore} instance. This is a convenience mechanism to eliminate the constant
     * need to call {@code client.getDataStore()} every time one needs to instantiate Resource.
     *
     * @param clazz the Resource class to instantiate.
     * @param <T>   the Resource sub-type
     * @return a new instance of the specified Resource.
     * @since 0.8
     */
    @Override
    public <T extends Resource> T instantiate(Class<T> clazz) {
        return this.dataStore.instantiate(clazz);
    }

    /**
     * Delegates to the internal {@code dataStore} instance. This is a convenience mechanism to eliminate the constant
     * need to call {@code client.getDataStore()} every time one needs to look up a Resource.
     *
     * @param href  the resource URL of the resource to retrieve
     * @param clazz the {@link Resource} sub-interface to instantiate
     * @param <T>   type parameter indicating the returned value is a {@link Resource} instance.
     * @return an instance of the specified class based on the data returned from the specified {@code href} URL.
     * @since 0.8
     */
    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz) {
        return this.dataStore.getResource(href, clazz);
    }
}






