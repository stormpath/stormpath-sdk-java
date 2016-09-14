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
package com.stormpath.sdk.servlet.config;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.lang.UnknownClassException;
import com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory;
import com.stormpath.sdk.servlet.i18n.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

/**
 * A {@code ConfigLoader} is responsible for loading a web application's Stormpath configuration at application
 * startup and making it available in the {@code ServletContext} as a {@link Config} instance.  Other ServletContext
 * aware instances loaded after this one can access the config instance by using the Config interface name as
 * the servlet context attribute key:
 * <p>
 * <pre>
 * Config config = (Config)servletContext.getAttribute(Config.class.getName());
 * </pre>
 * <p>
 * <h4>ConfigFactory</h4>
 * <p>
 * <p>The {@code ConfigLoader} will delegate config creation to a
 * {@link com.stormpath.sdk.servlet.config.ConfigFactory ConfigFactory} instance to actually create the {@link Config}
 * that will be made available to the application.  By default, the
 * {@link com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory} implementation will be used to create the
 * {@link Config} instance from a number of {@code .properties} file locations, system properties and environment
 * variables.</p>
 * <p>
 * <h4>Custom ConfigFactory Implementation</h4>
 * <p>
 * <p>If you need to create a custom ConfigFactory implementation, you can implement the
 * {@link com.stormpath.sdk.servlet.config.ConfigFactory} interface yourself and specify that implementation class name
 * as follows:</p>
 * <p>
 * <pre>
 * &lt;context-param&gt;
 *     &lt;param-name&gt;stormpathConfigFactoryClass&lt;/param-name&gt;
 *     &lt;param-value&gt;<b>com.mycompany.myapp.stormpath.MyCustomConfigFactory</b>&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * <p>
 * <p>And that class will be instantiated and used to create the Config instance.</p>
 * <p>
 * <p>If you do not specify this context-param, {@link com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory
 * com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory}
 * is assumed by default.</p>
 *
 * @see com.stormpath.sdk.servlet.config.ConfigFactory
 * @see com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory
 * @since 1.0.RC3
 */
public class ConfigLoader {

    /**
     * Servlet Context config param name used to specify the {@link ConfigFactory} implementation class to use when
     * creating a new {@link Config} instance at web app startup: {@code stormpathConfigFactoryClass}
     */
    public static final String CONFIG_FACTORY_CLASS_PARAM_NAME = "stormpathConfigFactoryClass";

    public static final String CONFIG_ATTRIBUTE_NAME = Config.class.getName();

    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);

    /**
     * Creates and initializes a Stormpath {@link Config} instance for the web application attributed to the
     * specified {@code ServletContext} and assigns that instance to the ServletContext for that context's lifespan.
     * The config instance is created by instantiating a {@link ConfigFactory} implementation class, and the
     * class name can be specified as a {@code stormpathConfigFactoryClass} context parameter.  If this parameter is
     * not specified, the default {@link com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory} implementation is
     * assumed.
     *
     * @param servletContext current servlet context
     * @return the new Stormpath {@code Config} instance.
     * @throws IllegalStateException if an existing Config instance has already been initialized and associated with
     *                               the specified {@code ServletContext}.
     */
    public Config createConfig(ServletContext servletContext) throws IllegalStateException {

        if (servletContext.getAttribute(CONFIG_ATTRIBUTE_NAME) != null) {
            String msg = "There is already a Stormpath Config instance associated with the current ServletContext.  " +
                "Check if you have multiple ConfigLoader* definitions in your web.xml or annotation config!";
            throw new IllegalStateException(msg);
        }

        servletContext.log("Initializing Stormpath config instance.");
        log.info("Starting Stormpath config initialization.");

        long startTime = System.currentTimeMillis();

        try {

            Config config = doCreateConfig(servletContext);

            servletContext.setAttribute(CONFIG_ATTRIBUTE_NAME, config);
            // needed for MessageTag implementation:
            servletContext.setAttribute(MessageContext.class.getName(), config.getMessageContext());

            // suppress log messages if stormpath disabled
            if (config.isStormpathEnabled()) {
                log.debug("Published Config as ServletContext attribute with name [{}]", CONFIG_ATTRIBUTE_NAME);

                if (log.isInfoEnabled()) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    log.info("Stormpath config initialized in {} ms.", elapsed);
                }
            } else {
                log.info("Stormpath disabled, cancelling initialization.");
            }

            return config;
        } catch (RuntimeException ex) {
            log.error("Stormpath config initialization failed", ex);
            servletContext.setAttribute(CONFIG_ATTRIBUTE_NAME, ex);
            throw ex;
        } catch (Error err) {
            log.error("Stormpath config initialization failed", err);
            servletContext.setAttribute(CONFIG_ATTRIBUTE_NAME, err);
            throw err;
        }
    }

    /**
     * Return the {@link ConfigFactory} implementation class to use, either the default
     * {@link com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory} or a custom class if specified.
     *
     * @param servletContext current servlet context
     * @return the ConfigFactory implementation class to use
     * @see #CONFIG_FACTORY_CLASS_PARAM_NAME
     * @see com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory
     */
    protected Class<?> determineConfigFactoryClass(ServletContext servletContext) {
        String className = servletContext.getInitParameter(CONFIG_FACTORY_CLASS_PARAM_NAME);
        className = Strings.trimWhitespace(className);
        if (className != null) {
            try {
                return Classes.forName(className);
            } catch (UnknownClassException ex) {
                throw new IllegalStateException("Failed to load custom ConfigFactory class [" + className + "]", ex);
            }
        } else {
            return DefaultConfigFactory.class;
        }
    }

    /**
     * Instantiates a {@link Config} based on the specified ServletContext.
     * <p>
     * This implementation {@link #determineConfigFactoryClass(javax.servlet.ServletContext) determines} a
     * {@link com.stormpath.sdk.servlet.config.ConfigFactory} implementation class to use.  That class is instantiated,
     * returned, and invoked.
     * </p>
     * <p>
     * This allows custom {@code ConfigFactory} implementations to be specified via a ServletContext init-param if
     * desired.  If not specified, the default {@link com.stormpath.sdk.servlet.config.impl.DefaultConfigFactory}
     * implementation will be used.
     * </p>
     *
     * @param sc current servlet context
     * @return the constructed Stormpath config instance
     */
    protected Config doCreateConfig(ServletContext sc) {

        Class<?> clazz = determineConfigFactoryClass(sc);

        if (!ConfigFactory.class.isAssignableFrom(clazz)) {
            throw new IllegalStateException("Custom ConfigFactory class [" + clazz.getName() +
                "] is not of required type [" + ConfigFactory.class.getName() + "]");
        }

        ConfigFactory factory = (ConfigFactory) Classes.newInstance(clazz);

        return factory.createConfig(sc);
    }

    /**
     * Destroys the {@link Config} for the given servlet context.
     *
     * @param servletContext the web apps servlet context
     */
    public void destroyConfig(ServletContext servletContext) {
        servletContext.log("Cleaning up Stormpath config.");
        servletContext.removeAttribute(CONFIG_ATTRIBUTE_NAME);
        servletContext.removeAttribute(MessageContext.class.getName());
    }
}

