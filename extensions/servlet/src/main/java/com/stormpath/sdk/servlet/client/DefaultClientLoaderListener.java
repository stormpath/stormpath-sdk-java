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
package com.stormpath.sdk.servlet.client;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Default bootstrap listener to startup and shutdown the web application's Stormpath
 * {@link com.stormpath.sdk.client.Client Client} instance at ServletContext startup and shutdown respectively.  This
 * class exists only to implement the {@link ServletContextListener} interface. All 'real' logic is done in the parent
 * {@link ClientLoader} class.
 *
 * <h3>Usage</h3>
 *
 * <p>Define the following in {@code web.xml}:</p>
 *
 * <pre>
 * &lt;listener&gt;
 *     &lt;listener-class&gt;<code>com.stormpath.sdk.servlet.client.DefaultClientLoaderListener</code>&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * &lt;context-param&gt;
 *     &lt;param-name&gt;stormpath.properties&lt;/param-name&gt;
 *     &lt;!-- Replace this next value with your web <a href="http://docs.stormpath.com/rest/product-guide/#locate-an-applications-rest-url">application's Stormpath HREF value</a> --&gt;
 *     &lt;param-value&gt;stormpath.application.href = <b>CHANGE_ME_TO_YOUR_APP_STORMPATH_HREF</b>&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 *
 * @see ClientLoader
 * @since 1.0
 */
public class DefaultClientLoaderListener extends ClientLoader implements ServletContextListener {

    /**
     * Initializes the Stormpath {@link com.stormpath.sdk.client.Client Client} instance and binds it to the
     * {@code ServletContext} at application startup for future reference.
     *
     * @param sce the ServletContextEvent triggered upon application startup
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        createClient(sce.getServletContext());
    }

    /**
     * Destroys any previously created/bound {@link com.stormpath.sdk.client.Client Client} instance on application
     * shutdown.
     *
     * @param sce the ServletContextEvent triggered upon application shutdown
     * @see #contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        destroyClient(sce.getServletContext());
    }
}
