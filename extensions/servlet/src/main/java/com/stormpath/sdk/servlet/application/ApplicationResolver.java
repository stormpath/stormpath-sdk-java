package com.stormpath.sdk.servlet.application;

import com.stormpath.sdk.application.Application;

import javax.servlet.ServletContext;

/**
 * Returns the Stormpath {@link Application} record that reflects the currently running web application.  Once
 * obtained, developers can use the {@code Application} instance to perform any identity operations necessary for
 * the web application, such as login, password reset, etc.  For example:
 *
 * <pre>
 * Application app = ApplicationResolver.INSTANCE.getApplication(aServletRequest.getServletContext());
 * </pre>
 *
 * @see DefaultApplicationResolver
 * @since 1.0
 */
public interface ApplicationResolver {

    /**
     * A thread-safe instance to use as desired.  The implementation is a
     * {@link com.stormpath.sdk.servlet.application.DefaultApplicationResolver DefaultApplicationResolver}.
     */
    public static final ApplicationResolver INSTANCE = new DefaultApplicationResolver();

    /**
     * Returns the Stormpath {@link Application} record that reflects the currently running web application.  For
     * example:
     *
     * <pre>
     * ApplicationResolver resolver = new {@link DefaultApplicationResolver DefaultApplicationResolver}();
     * Application application = resolver.getApplication(aServletRequest.getServletContext());
     * </pre>
     *
     * @param servletContext the web application's {@code servletContext} created by the servlet container at
     *                       application startup.
     * @return the Stormpath {@link Application} instance that reflects the currently running web application.
     */
    Application getApplication(ServletContext servletContext);
}
