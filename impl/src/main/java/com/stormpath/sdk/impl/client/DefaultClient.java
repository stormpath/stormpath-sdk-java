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
package com.stormpath.sdk.impl.client;

import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.ApiKey;
import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.tenant.Tenant;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * The default {@link Client} implementation.
 * </p>
 * <h3>DataStore API</h3>
 * As of 0.8, this class implements the {@link DataStore} interface, but this implementation merely acts as a wrapper to
 * the underlying 'real' {@code DataStore} instance. This is a convenience mechanism to eliminate the constant need to
 * call {@code client.getDataStore()} every time one needs to instantiate or look up a Resource.
 *
 * @see <a href="http://www.stormpath.com/docs/quickstart/connect">Communicating with Stormpath: Get your API Key</a>
 * @since 1.0.alpha
 */
public class DefaultClient implements Client {

    private final DataStore dataStore;

    private String currentTenantHref;


    /**
     * Instantiates a new Client instance that will communicate with the Stormpath REST API.  See the class-level
     * JavaDoc for a usage example.
     *
     * @param apiKey the Stormpath account API Key that will be used to authenticate the client with Stormpath's API server
     * @param baseUrl the Stormpath base URL
     * @param proxy the HTTP proxy to be used when communicating with the Stormpath API server (can be null)
     * @param cacheManager the {@link com.stormpath.sdk.cache.CacheManager} that should be used to cache Stormpath REST resources (can be null)
     * @param authenticationScheme the HTTP authentication scheme to be used when communicating with the Stormpath API server (can be null)
     */
    public DefaultClient(ApiKey apiKey, String baseUrl, Proxy proxy, CacheManager cacheManager, AuthenticationScheme authenticationScheme) {
        Assert.notNull(apiKey, "apiKey argument cannot be null.");
        Object requestExecutor = createRequestExecutor(apiKey, proxy, authenticationScheme);
        DataStore ds = createDataStore(requestExecutor, baseUrl);

        if (cacheManager != null) {
            // TODO: remove when we have a proper Builder interfaces. See https://github.com/stormpath/stormpath-sdk-java/issues/8
            applyCacheManager(ds, cacheManager);
        }

        this.dataStore = ds;
    }

    private void applyCacheManager(DataStore dataStore, CacheManager cacheManager) {
        Class<?> clazz = dataStore.getClass();
        try {
            Method method = clazz.getDeclaredMethod("setCacheManager", CacheManager.class);
            method.setAccessible(true);
            method.invoke(dataStore, cacheManager);
        } catch (Exception e) {
            String msg = "Unable to apply cacheManager instance on DataStore implementation " + clazz;
            throw new RuntimeException(msg);
        }
    }

    @Override
    public Tenant getCurrentTenant() {
        String href = currentTenantHref;
        if (href == null) {
            href = "/tenants/current";
        }
        Tenant current = this.dataStore.getResource(href, Tenant.class);
        this.currentTenantHref = current.getHref();
        return current;
    }

    @Override
    public DataStore getDataStore() {
        return this.dataStore;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object createRequestExecutor(ApiKey apiKey, Proxy proxy, AuthenticationScheme authenticationScheme) {

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

        Constructor ctor = Classes.getConstructor(requestExecutorClass, ApiKey.class, Proxy.class, AuthenticationScheme.class);

        return Classes.instantiate(ctor, apiKey, proxy, authenticationScheme);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
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
     */
    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz) {
        return this.dataStore.getResource(href, clazz);
    }
}
