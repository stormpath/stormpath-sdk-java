/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.application;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Default bootstrap listener to acquire and release the web application's Stormpath {@link
 * com.stormpath.sdk.application.Application Application} resource at ServletContext startup and shutdown respectively.
 * This class exists only to implement the {@link ServletContextListener} interface. All 'real' logic is done in the
 * parent {@link com.stormpath.sdk.servlet.application.ApplicationLoader} implementation.
 *
 * <h3>Simple Usage</h3>
 *
 * <ol> <li>Ensure that {@link com.stormpath.sdk.servlet.config.DefaultConfigLoaderListener} and {@link
 * com.stormpath.sdk.servlet.client.DefaultClientLoaderListener} have been defined as listeners in web.xml.</li>
 * <li><p>Define the following <em>after</em> the above two listeners in {@code web.xml}:</p>
 * <pre>
 * &lt;listener&gt;
 *     &lt;listener-class&gt;<code>com.stormpath.sdk.servlet.application.DefaultApplicationLoaderListener</code>&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * </pre>
 * </li> </ol>
 *
 * @see com.stormpath.sdk.servlet.config.ConfigLoader ConfigLoader
 * @since 1.0.RC3
 */
public class DefaultApplicationLoaderListener extends ApplicationLoader implements ServletContextListener {

    /**
     * Initializes the Stormpath {@link com.stormpath.sdk.application.Application Application} instance and binds it to
     * the {@code ServletContext} at application startup for future reference.
     *
     * @param sce the ServletContextEvent triggered upon application startup
     * @see #contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Config config = ConfigResolver.INSTANCE.getConfig(sce.getServletContext());
        if (config.isStormpathEnabled()) {
            getApplication(sce.getServletContext());
        }
    }

    /**
     * Removes any previously registered {@link com.stormpath.sdk.application.Application Application} instance on
     * application shutdown.
     *
     * @param sce the ServletContextEvent triggered upon application shutdown
     * @see #contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        removeApplication(sce.getServletContext());
    }
}
