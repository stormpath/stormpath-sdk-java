package com.stormpath.sdk.servlet.application;

import com.stormpath.sdk.application.Application;

import javax.servlet.ServletContext;

/**
 * Looks up and returns the Stormpath {@link Application} record that reflects the currently running web application.
 * Once obtained and saved by the {@link com.stormpath.sdk.servlet.application.ApplicationLoader ApplicationLoader},
 * developers can use the {@code Application} instance to perform any identity operations necessary for
 * the web application, such as login, password reset, etc.  For example:
 *
 * <pre>
 * Application app = Servlets.getApplication(aServletRequest);
 * </pre>
 *
 * @see com.stormpath.sdk.servlet.Servlets
 * @see DefaultApplicationResolver
 * @since 1.0
 */
public interface ApplicationResolver {

    /**
     * Returns the Stormpath {@link Application} record that reflects the currently running web application.
     *
     * @param servletContext the web application's {@code servletContext} created by the servlet container at
     *                       application startup.
     * @return the Stormpath {@link Application} instance that reflects the currently running web application.
     */
    Application getApplication(ServletContext servletContext);
}
