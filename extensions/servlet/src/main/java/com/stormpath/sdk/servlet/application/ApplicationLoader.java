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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.lang.UnknownClassException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

/**
 * A {@code ApplicationLoader} is responsible for loading a web application's Stormpath {@link
 * com.stormpath.sdk.application.Application} resource at application startup and making it available in the {@code
 * ServletContext}.  Other ServletContext aware components loaded after this one can access the Application instance by
 * using the Application interface name as the servlet context attribute key:
 *
 * <pre>
 * Application app = (Application)servletContext.getAttribute(Application.class.getName());
 * </pre>
 *
 * <h4>ApplicationResolver</h4>
 *
 * <p>The {@code ApplicationLoader} will delegate application instance lookup to an {@link ApplicationResolver
 * ApplicationResolver} implementation.  By default, the {@link com.stormpath.sdk.servlet.application.DefaultApplicationResolver}
 * implementation will be used.  See that implementation's {@link com.stormpath.sdk.servlet.application.DefaultApplicationResolver
 * JavaDoc} to learn about how it discovers the application instance.</p>
 *
 * <h4>Custom {@code ApplicationResolver} Implementation</h4>
 *
 * <p>If you need to create a custom ApplicationResolver implementation, you can implement the {@link
 * com.stormpath.sdk.servlet.application.ApplicationResolver ApplicationResolver} interface yourself and specify that
 * implementation class name as follows:</p>
 *
 * <pre>
 * &lt;context-param&gt;
 *     &lt;param-name&gt;stormpathApplicationResolverClass&lt;/param-name&gt;
 *     &lt;param-value&gt;<b>com.mycompany.myapp.stormpath.MyCustomApplicationResolver</b>&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 *
 * <p>And that class will be instantiated and used to lookup the Application instance.</p>
 *
 * <p>If you do not specify this context-param, {@link com.stormpath.sdk.servlet.application.DefaultApplicationResolver}
 * is assumed by default.</p>
 *
 * @see com.stormpath.sdk.servlet.application.ApplicationResolver
 * @see com.stormpath.sdk.servlet.application.DefaultApplicationResolver
 * @since 1.0.RC3
 */
public class ApplicationLoader {

    /**
     * Servlet Context config param name used to specify the {@link com.stormpath.sdk.servlet.application.ApplicationResolver}
     * implementation class to use when obtaining the app's Stormpath {@link com.stormpath.sdk.application.Application
     * Application} resource at startup: {@code stormpathApplicationResolverClass}
     */
    public static final String APP_RESOLVER_CLASS_PARAM_NAME = "stormpathApplicationResolverClass";

    public static final String APP_ATTRIBUTE_NAME = Application.class.getName();

    private static final Logger log = LoggerFactory.getLogger(ApplicationLoader.class);

    /**
     * Looks up and returns the web application's registered Stormpath {@link Application} instance and assigns that
     * instance to the ServletContext for that context's lifespan.
     *
     * <p>The app is obtained by instantiating a {@link com.stormpath.sdk.servlet.application.ApplicationResolver}
     * implementation class, and the class name can be specified as a {@code stormpathApplicationResolverClass} context
     * parameter.  If this parameter is not specified, the default {@link com.stormpath.sdk.servlet.application.DefaultApplicationResolver}
     * implementation is assumed.
     *
     * @param servletContext current servlet context
     * @return the new Stormpath {@code Application} instance.
     * @throws IllegalStateException if an existing Application instance has already been initialized and associated
     *                               with the specified {@code ServletContext}.
     */
    public Application getApplication(ServletContext servletContext) throws IllegalStateException {

        if (servletContext.getAttribute(APP_ATTRIBUTE_NAME) != null) {
            String msg = "There is already a Stormpath Application instance associated with the current " +
                         "ServletContext.  Check if you have multiple ApplicationLoader* definitions in your web.xml " +
                         "or annotation config!";
            throw new IllegalStateException(msg);
        }

        servletContext.log("Initializing Stormpath Application instance.");
        log.info("Starting Stormpath Application initialization.");

        long startTime = System.currentTimeMillis();

        try {

            Application app = doGetApplication(servletContext);

            servletContext.setAttribute(APP_ATTRIBUTE_NAME, app);

            log.debug("Published Application resource as ServletContext attribute with name [{}]", APP_ATTRIBUTE_NAME);

            if (log.isInfoEnabled()) {
                long elapsed = System.currentTimeMillis() - startTime;
                log.info("Stormpath Application initialized in {} ms.", elapsed);
            }

            return app;
        } catch (RuntimeException ex) {
            log.error("Stormpath application initialization failed", ex);
            servletContext.setAttribute(APP_ATTRIBUTE_NAME, ex);
            throw ex;
        } catch (Error err) {
            log.error("Stormpath application initialization failed", err);
            servletContext.setAttribute(APP_ATTRIBUTE_NAME, err);
            throw err;
        }
    }

    /**
     * Return the {@link com.stormpath.sdk.servlet.application.ApplicationResolver} implementation class to use, either
     * the default {@link com.stormpath.sdk.servlet.application.DefaultApplicationResolver} or a custom class if
     * specified.
     *
     * @param servletContext current servlet context
     * @return the ApplicationResolver implementation class to use
     * @see #APP_RESOLVER_CLASS_PARAM_NAME
     * @see com.stormpath.sdk.servlet.application.DefaultApplicationResolver
     */
    protected Class<?> determineApplicationResolverClass(ServletContext servletContext) {
        String className = servletContext.getInitParameter(APP_RESOLVER_CLASS_PARAM_NAME);
        className = Strings.trimWhitespace(className);
        if (className != null) {
            try {
                return Classes.forName(className);
            } catch (UnknownClassException ex) {
                String msg = "Failed to load custom ApplicationResolver class [" + className + "]";
                throw new IllegalStateException(msg, ex);
            }
        } else {
            return DefaultApplicationResolver.class;
        }
    }

    /**
     * Looks up an Application based on the specified ServletContext.
     *
     * <p>This implementation {@link #determineApplicationResolverClass(javax.servlet.ServletContext) determines} a
     * {@link com.stormpath.sdk.application.Application} implementation class to use.  That class is instantiated,
     * returned, and invoked.</p>
     *
     * <p>This allows a custom {@code ApplicationResolver} implementation to be specified via a ServletContext
     * init-param if desired.  If not specified, the default {@link com.stormpath.sdk.servlet.application.DefaultApplicationResolver}
     * implementation will be used.</p>
     *
     * @param sc current servlet context
     * @return the constructed Stormpath config instance
     */
    protected Application doGetApplication(ServletContext sc) {

        Class<?> clazz = determineApplicationResolverClass(sc);

        if (!ApplicationResolver.class.isAssignableFrom(clazz)) {
            throw new IllegalStateException("Custom ApplicationResolver class [" + clazz.getName() +
                                            "] is not of required type [" + ApplicationResolver.class.getName() + "]");
        }

        ApplicationResolver resolver = (ApplicationResolver) Classes.newInstance(clazz);

        return resolver.getApplication(sc);
    }

    /**
     * Unbinds the {@link Application} instance for the given servlet context.
     *
     * @param servletContext the web apps servlet context
     */
    public void removeApplication(ServletContext servletContext) {
        servletContext.log("Cleaning up Stormpath application.");
        servletContext.removeAttribute(APP_ATTRIBUTE_NAME);
    }
}


