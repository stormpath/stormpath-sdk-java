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
package com.stormpath.sdk.servlet.client;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.lang.UnknownClassException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

/**
 * A {@code ClientLoader} is responsible for loading a web application's Stormpath {@link Client} instance and making it
 * available in the {@code ServletContext} at application startup.  Once in the {@code ServletContext}, the
 * {@code Client} is available to any application component that needs to interact with Stormpath; these components can
 * can access the {@code client} instance easily, for example:
 *
 * <pre>
 * (Client)aServletRequest.getAttribute(Client.class.getName());
 * </pre>
 *
 * <h3>Usage</h3>
 *
 * <p>This implementation will, without any configuration at all, instantiate and use a
 * {@link DefaultServletContextClientFactory} to build the {@link Client} used by the application.  The
 * {@code DefaultServletContextClientFactory} supports the following {@code context-param} options in your
 * {@code web.xml} configuration: {@code stormpathApiKeyFileLocation} and {@code stormpathClientAuthenticationScheme}.
 * </p>
 *
 * <h4>{@code stormpathApiKeyFileLocation}</h4>
 *
 * <p>The {@code stormpathApiKeyFileLocation} {@code context-param}, if present, allows you to specify the resource
 * path to the {@code apiKey.properties} file that should be used to authenticate requests to Stormpath.  For example:</p>
 *
 * <pre>
 * &lt;context-param&gt;
 *     &lt;param-name&gt;stormpathApiKeyFileLocation&lt;/param-name&gt;
 *     &lt;param-value&gt;/users/whatever/stormpath/apiKey.properties&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 *
 * <p>You may load files from the filesystem, classpath, or URLs by prefixing the location path with
 * {@code file:}, {@code classpath:}, or {@code url:} respectively.  If no prefix is present, {@code file:}
 * is assumed by default.</p>
 *
 * <p>If you do not specify this context-param, <code>${user.home}/.stormpath/apiKey.properties</code> is assumed by
 * default.</p>
 *
 * <h4>{@code stormpathClientAuthenticationScheme}</h4>
 *
 * <p>The {@code stormpathClientAuthenticationScheme} {@code context-param}, if present, allows you to specify a
 * different HTTP authentication scheme than Stormpath's (very secure) default.  At the moment, the only other
 * alternative is {@code basic}. For example:</p>
 *
 * <pre>
 * &lt;context-param&gt;
 *     &lt;param-name&gt;stormpathClientAuthenticationScheme&lt;/param-name&gt;
 *     &lt;param-value&gt;basic&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 *
 * <p><b>Because Stormpath's default authentication scheme is more secure than HTTP Basic authentication, it is
 * strongly recommended that you do not override this value unless you have to</b>.  For example, Google App Engine
 * users will be forced to specify {@code basic} here because GAE's outbound HTTP connection behavior interferes with
 * the default algorithm.  Again, it is not recommended that you use {@code basic} unless you have a technical
 * limitation that prevents you from using Stormpath's default.</p>
 *
 * <p>If you do not specify this context-param,
 * <a href="http://docs.stormpath.com/rest/product-guide/#authentication-digest"><code>sauthc1</code></a>
 * is assumed by default.</p>
 *
 * <h4>Custom ServletContextClientFactory Implementation</h4>
 *
 * <p>As mentioned above, the {@code ClientLoader} assumes the {@code DefaultServletContextClientFactory} behavior
 * (supporting the above two {@code context-param} options) is sufficient for many needs.  If you need to build your
 * application's {@code Client} instance in a custom way - perhaps to enable specific
 * {@link com.stormpath.sdk.cache.CacheManager CacheManager} instance using a
 * {@link com.stormpath.sdk.client.Clients#builder() ClientBuilder} - you can implement the
 * {@link ServletContextClientFactory} interface yourself and specify that implementation class name as
 * follows:</p>
 *
 * <pre>
 * &lt;context-param&gt;
 *     &lt;param-name&gt;stormpathServletContextClientFactoryClass&lt;/param-name&gt;
 *     &lt;param-value&gt;<b>com.mycompany.myapp.stormpath.MyCustomServletContextClientFactory</b>&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 *
 * <p>If you do not specify this context-param, {@link DefaultServletContextClientFactory
 * com.stormpath.sdk.servlet.client.DefaultServletContextClientFactory} is assumed by default.</p>
 *
 * <p><b>Because the {@code DefaultServletContextClientFactory} implementation is basic and does not support specifying
 * a custom CacheManager implementation, it is recommended that you create your own
 * {@link ServletContextClientFactory} implementation and specify that class name here for more advanced use cases.
 * </b></p>
 *
 * @see ServletContextClientFactory
 * @see DefaultServletContextClientFactory
 * @since 1.0.RC3
 */
public class ClientLoader {

    /**
     * Servlet Context config param for specifying the {@link Client} implementation class to use:
     * {@code stormpathServletContextClientFactoryClass}
     */
    public static final String CLIENT_FACTORY_CLASS_PARAM = "stormpathServletContextClientFactoryClass";

    public static final String CLIENT_ATTRIBUTE_KEY = Client.class.getName();

    private static final Logger log = LoggerFactory.getLogger(ClientLoader.class);

    /**
     * Creates and initializes a Stormpath {@link Client} instance for the web application attributed to the
     * specified {@code ServletContext} and assigns that instance to the ServletContext for that context's lifespan.
     * The client instance is created by instantiating a {@link ServletContextClientFactory} implementation class, and the
     * class name can be specified as a {@code stormpathClientFactoryClass} context parameter.  If this
     * parameter is not specified, the default {@link DefaultServletContextClientFactory} implementation is assumed.
     *
     * @param servletContext current servlet context
     * @return the new Stormpath {@code Client} instance.
     * @throws IllegalStateException if an existing Client has already been initialized and associated with
     *                               the specified {@code ServletContext}.
     */
    public Client createClient(ServletContext servletContext) throws IllegalStateException {

        if (servletContext.getAttribute(CLIENT_ATTRIBUTE_KEY) != null) {
            String msg = "There is already a Stormpath client instance associated with the current ServletContext.  " +
                    "Check if you have multiple ClientLoader* definitions in your web.xml or annotation config!";
            throw new IllegalStateException(msg);
        }

        servletContext.log("Initializing Stormpath client instance.");
        log.info("Starting Stormpath client initialization.");

        long startTime = System.currentTimeMillis();

        try {

            Client client = doCreateClient(servletContext);
            servletContext.setAttribute(CLIENT_ATTRIBUTE_KEY, client);

            log.debug("Published Client as ServletContext attribute with name [{}]", CLIENT_ATTRIBUTE_KEY);

            if (log.isInfoEnabled()) {
                long elapsed = System.currentTimeMillis() - startTime;
                log.info("Stormpath client initialized in {} ms.", elapsed);
            }

            return client;
        } catch (RuntimeException ex) {
            log.error("Stormpath client initialization failed", ex);
            servletContext.setAttribute(CLIENT_ATTRIBUTE_KEY, ex);
            throw ex;
        } catch (Error err) {
            log.error("Stormpath client initialization failed", err);
            servletContext.setAttribute(CLIENT_ATTRIBUTE_KEY, err);
            throw err;
        }
    }

    /**
     * Return the {@link ServletContextClientFactory} implementation class to use, either the default {@link DefaultServletContextClientFactory}
     * or a custom class if specified.
     *
     * @param servletContext current servlet context
     * @return the WebEnvironment implementation class to use
     * @see #CLIENT_FACTORY_CLASS_PARAM
     * @see DefaultServletContextClientFactory
     */
    protected Class<?> determineClientFactoryClass(ServletContext servletContext) {
        String className = servletContext.getInitParameter(CLIENT_FACTORY_CLASS_PARAM);
        className = Strings.trimWhitespace(className);
        if (className != null) {
            try {
                return Classes.forName(className);
            } catch (UnknownClassException ex) {
                throw new IllegalStateException("Failed to load custom ServletContextClientFactory class [" + className + "]", ex);
            }
        } else {
            return DefaultServletContextClientFactory.class;
        }
    }

    /**
     * Instantiates a {@link Client} based on the specified ServletContext.
     * <p>
     * This implementation {@link #determineClientFactoryClass(javax.servlet.ServletContext) determines} a
     * {@link ServletContextClientFactory} implementation class to use.  That class is instantiated, returned, and invoked.
     * </p>
     * <p>
     * This allows custom {@code ServletContextClientFactory} implementations to be specified via a ServletContext init-param if
     * desired.  If not specified, the default {@link DefaultServletContextClientFactory} implementation will be used.
     * </p>
     *
     * @param sc current servlet context
     * @return the constructed Stormpath client instance
     */
    protected Client doCreateClient(ServletContext sc) {

        Class<?> clazz = determineClientFactoryClass(sc);
        if (!ServletContextClientFactory.class.isAssignableFrom(clazz)) {
            throw new IllegalStateException("Custom ServletContextClientFactory class [" + clazz.getName() +
                    "] is not of required type [" + ServletContextClientFactory.class.getName() + "]");
        }

        ServletContextClientFactory factory = (ServletContextClientFactory) Classes.newInstance(clazz);

        return factory.createClient(sc);
    }

    /**
     * Destroys the {@link Client} for the given servlet context.
     *
     * @param servletContext the ServletContext attributed to the WebSecurityManager
     */
    public void destroyClient(ServletContext servletContext) {
        servletContext.log("Cleaning up Stormpath client.");
        servletContext.removeAttribute(CLIENT_ATTRIBUTE_KEY);
    }
}
