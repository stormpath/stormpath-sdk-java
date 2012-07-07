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
import com.stormpath.sdk.tenant.Tenant;

import java.io.InputStream;
import java.lang.reflect.Constructor;

/**
 * The {@code Client} is the main entry point to the Stormpath Java SDK.  A Java project wishing to
 * communicate with the Stormpath REST API service must instantiate a {@code Client} instance.  After obtaining
 * a {@code Client instance}, the REST API may be used by making simple Java calls on objects returned from
 * the Client (or any children objects obtained therein).
 * <p/>
 * For example:
 * <pre>
 * String accessId = //<a href="http://www.stormpath.com/docs/quickstart/connect">Your Stormpath API Key's Access ID</a>
 * String secret = //<a href="http://www.stormpath.com/docs/quickstart/connect">Your Stormpath API Key's Secret</a>
 *
 * //create the Client instance:
 * Client client = new Client(new DefaultApiKey(accessId, secret));
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
 *
 * @since 0.1
 * @see <a href="http://www.stormpath.com/docs/quickstart/connect">Communicating with Stormpath: Get your API Key</a>
 */
public class Client {

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
        Object requestExecutor = createRequestExecutor(apiKey);
        this.dataStore = createDataStore(requestExecutor, DEFAULT_API_VERSION);
    }

    //no modifier on purpose: for local development testing only:
    Client(ApiKey apiKey, String baseUrl) {
        Object requestExecutor = createRequestExecutor(apiKey);
        this.dataStore = createDataStore(requestExecutor, baseUrl);
    }

    public Tenant getCurrentTenant() {
        return this.dataStore.getResource("/tenants/current", Tenant.class);
    }

    public DataStore getDataStore() {
        return this.dataStore;
    }

    //since 0.3
    @SuppressWarnings("unchecked")
    private Object createRequestExecutor(ApiKey apiKey) {

        String className = "com.stormpath.sdk.impl.http.httpclient.HttpClientRequestExecutor";

        Class requestExecutorClass = null;

        if (ClassUtils.isAvailable(className)) {
            requestExecutorClass = ClassUtils.forName(className);
        } else {
            //we might be able to check for other implementations in the future, but for now, we only support
            //HTTP calls via the HttpClient.  Throw an exception:

            String msg = "Unable to find the '" + className + "' implementation on the classpath.  Please ensure you " +
                    "have added the stormpath-sdk-impl-httpclient .jar file to your runtime classpath.";
            throw new RuntimeException(msg);
        }

        Constructor ctor = ClassUtils.getConstructor(requestExecutorClass, ApiKey.class);

        return ClassUtils.instantiate(ctor, apiKey);
    }

    //@since 0.3
    @SuppressWarnings("unchecked")
    private DataStore createDataStore(Object requestExecutor, Object secondCtorArg) {

        String requestExecutorInterfaceClassName = "com.stormpath.sdk.impl.http.RequestExecutor";
        Class requestExecutorInterfaceClass;

        try {
            requestExecutorInterfaceClass = ClassUtils.forName(requestExecutorInterfaceClassName);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to load required interface: " + requestExecutorInterfaceClassName +
                    ".  Please ensure you have added the stormpath-sdk-impl .jar file to your runtime classpath.", t);
        }

        String className = "com.stormpath.sdk.impl.ds.DefaultDataStore";
        Class dataStoreClass;

        try {
            dataStoreClass = ClassUtils.forName(className);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to load default DataStore implementation class: " +
                    className + ".  Please ensure you have added the stormpath-sdk-impl .jar file to your " +
                    "runtime classpath.", t);
        }

        Constructor ctor = ClassUtils.getConstructor(dataStoreClass, requestExecutorInterfaceClass, secondCtorArg.getClass());

        try {
            return (DataStore) ctor.newInstance(requestExecutor, secondCtorArg);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to instantiate DataStore implementation: " + className, t);
        }
    }

    //since 0.3
    private static class ClassUtils {

        private static final ClassLoaderAccessor THREAD_CL_ACCESSOR = new ExceptionIgnoringAccessor() {
            @Override
            protected ClassLoader doGetClassLoader() throws Throwable {
                return Thread.currentThread().getContextClassLoader();
            }
        };

        private static final ClassLoaderAccessor CLASS_CL_ACCESSOR = new ExceptionIgnoringAccessor() {
            @Override
            protected ClassLoader doGetClassLoader() throws Throwable {
                return ClassUtils.class.getClassLoader();
            }
        };

        private static final ClassLoaderAccessor SYSTEM_CL_ACCESSOR = new ExceptionIgnoringAccessor() {
            @Override
            protected ClassLoader doGetClassLoader() throws Throwable {
                return ClassLoader.getSystemClassLoader();
            }
        };

        /**
         * Attempts to load the specified class name from the current thread's
         * {@link Thread#getContextClassLoader() context class loader}, then the
         * current ClassLoader (<code>ClassUtils.class.getClassLoader()</code>), then the system/application
         * ClassLoader (<code>ClassLoader.getSystemClassLoader()</code>, in that order.  If any of them cannot locate
         * the specified class, an <code>UnknownClassException</code> is thrown (our RuntimeException equivalent of
         * the JRE's <code>ClassNotFoundException</code>.
         *
         * @param fqcn the fully qualified class name to load
         * @return the located class
         * @throws RuntimeException if the class cannot be found.
         */
        public static Class forName(String fqcn) throws RuntimeException {

            Class clazz = THREAD_CL_ACCESSOR.loadClass(fqcn);

            if (clazz == null) {
                clazz = CLASS_CL_ACCESSOR.loadClass(fqcn);
            }

            if (clazz == null) {
                clazz = SYSTEM_CL_ACCESSOR.loadClass(fqcn);
            }

            if (clazz == null) {
                String msg = "Unable to load class named [" + fqcn + "] from the thread context, current, or " +
                        "system/application ClassLoaders.  All heuristics have been exhausted.  Class could not be found.";
                throw new RuntimeException(msg);
            }

            return clazz;
        }

        public static boolean isAvailable(String fullyQualifiedClassName) {
            try {
                forName(fullyQualifiedClassName);
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }

        public static <T> Constructor<T> getConstructor(Class<T> clazz, Class... argTypes) {
            try {
                return clazz.getConstructor(argTypes);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }

        }

        public static <T> T instantiate(Constructor<T> ctor, Object... args) {
            try {
                return ctor.newInstance(args);
            } catch (Exception e) {
                String msg = "Unable to instantiate instance with constructor [" + ctor + "]";
                throw new RuntimeException(msg, e);
            }
        }

        private static interface ClassLoaderAccessor {
            Class loadClass(String fqcn);

            InputStream getResourceStream(String name);
        }

        private static abstract class ExceptionIgnoringAccessor implements ClassLoaderAccessor {

            public Class loadClass(String fqcn) {
                Class clazz = null;
                ClassLoader cl = getClassLoader();
                if (cl != null) {
                    try {
                        clazz = cl.loadClass(fqcn);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
                return clazz;
            }

            public InputStream getResourceStream(String name) {
                InputStream is = null;
                ClassLoader cl = getClassLoader();
                if (cl != null) {
                    is = cl.getResourceAsStream(name);
                }
                return is;
            }

            protected final ClassLoader getClassLoader() {
                try {
                    return doGetClassLoader();
                } catch (Throwable ignored) {
                }
                return null;
            }

            protected abstract ClassLoader doGetClassLoader() throws Throwable;
        }
    }

}






