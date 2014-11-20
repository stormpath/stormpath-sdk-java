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
package com.stormpath.sdk.servlet.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Default bootstrap listener to startup and shutdown the web application's Stormpath {@link Config Config} instance at
 * ServletContext startup and shutdown respectively.  This class exists only to implement the {@link
 * ServletContextListener} interface. All 'real' logic is done in the parent {@link
 * com.stormpath.sdk.servlet.config.ConfigLoader} implementation.
 *
 * <h3>Simple Usage</h3>
 *
 * <ol><li><p>Define the following in {@code web.xml}:</p>
 * <pre>
 * &lt;listener&gt;
 *     &lt;listener-class&gt;<code>com.stormpath.sdk.servlet.config.DefaultConfigLoaderListener</code>&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * </pre>
 * </li> <li>Add a <code>/WEB-INF/stormpath.properties</code> config file in your .war, or alternatively put a
 * <code>stormpath.properties</code> at the root of the classpath.</code></li> </ol>
 *
 * @see com.stormpath.sdk.servlet.config.ConfigLoader ConfigLoader
 * @since 1.0
 */
public class DefaultConfigLoaderListener extends ConfigLoader implements ServletContextListener {

    /**
     * Initializes the Stormpath {@link Config Config} instance and binds it to the {@code ServletContext} at
     * application startup for future reference.
     *
     * @param sce the ServletContextEvent triggered upon application startup
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        createConfig(sce.getServletContext());
    }

    /**
     * Destroys any previously created/bound {@link Config Config} instance on application shutdown.
     *
     * @param sce the ServletContextEvent triggered upon application shutdown
     * @see #contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        destroyConfig(sce.getServletContext());
    }
}
