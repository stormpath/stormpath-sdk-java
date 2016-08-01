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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * A {@link Filter} for serving static files using the Servlet container's &quot;default&quot; Servlet.
 * <p>
 * <p>This filter must be ordered as the last filter in the chain so that it will only execute when other more
 * specific filter mappings (e.g. controllers) can be matched.</p>
 * <p>
 * <p>Requests are handled by forwarding through the {@link RequestDispatcher} obtained via the
 * name specified through the {@link #setDefaultServletName "defaultServletName" property}.
 * In most cases, the {@code defaultServletName} does not need to be set explicitly, as the
 * handler checks at initialization time for the presence of the default Servlet of well-known
 * containers such as Tomcat, Jetty, Resin, WebLogic and WebSphere. However, when running in a
 * container where the default Servlet's name is not known, or where it has been customized
 * via server configuration, the {@code defaultServletName} will need to be set explicitly.</p>
 * <p>
 * <p>Attribution: most of this logic was gratefully acquired from the Spring Framework's
 * {@code DefaultServletHttpRequestHandler}. Author attribution and Apache 2.0 license remain in-tact.</p>
 *
 * @author Jeremy Grelle
 * @author Juergen Hoeller
 * @since 1.0.0
 */
public class StaticResourceFilter implements Filter, ServletContextInitializable {

    /**
     * Default Servlet name used by Tomcat, Jetty, JBoss, and GlassFish
     */
    private static final String COMMON_DEFAULT_SERVLET_NAME = "default";

    /**
     * Default Servlet name used by Google App Engine
     */
    private static final String GAE_DEFAULT_SERVLET_NAME = "_ah_default";

    /**
     * Default Servlet name used by Resin
     */
    private static final String RESIN_DEFAULT_SERVLET_NAME = "resin-file";

    /**
     * Default Servlet name used by WebLogic
     */
    private static final String WEBLOGIC_DEFAULT_SERVLET_NAME = "FileServlet";

    /**
     * Default Servlet name used by WebSphere
     */
    private static final String WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";

    private String defaultServletName;

    private ServletContext servletContext;

    public void setDefaultServletName(String defaultServletName) {
        this.defaultServletName = defaultServletName;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        init(filterConfig.getServletContext());
    }

    /**
     * If the {@code defaultServletName} property has not been explicitly set,
     * attempts to locate the default Servlet using the known common
     * container-specific names.
     */
    @Override
    public void init(ServletContext servletContext) throws ServletException {
        this.servletContext = servletContext;
        if (!Strings.hasText(this.defaultServletName)) {
            if (this.servletContext.getNamedDispatcher(COMMON_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = COMMON_DEFAULT_SERVLET_NAME;
            } else if (this.servletContext.getNamedDispatcher(GAE_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = GAE_DEFAULT_SERVLET_NAME;
            } else if (this.servletContext.getNamedDispatcher(RESIN_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = RESIN_DEFAULT_SERVLET_NAME;
            } else if (this.servletContext.getNamedDispatcher(WEBLOGIC_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = WEBLOGIC_DEFAULT_SERVLET_NAME;
            } else if (this.servletContext.getNamedDispatcher(WEBSPHERE_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = WEBSPHERE_DEFAULT_SERVLET_NAME;
            } else {
                throw new IllegalStateException("Unable to locate the default servlet for serving static content. " +
                    "Please set the 'defaultServletName' property explicitly.");
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RequestDispatcher rd = this.servletContext.getNamedDispatcher(this.defaultServletName);
        if (rd == null) {
            throw new IllegalStateException("A RequestDispatcher could not be located for the default servlet '" +
                this.defaultServletName + "'");
        }
        rd.forward(request, response);
    }

    @Override
    public void destroy() {
    }
}
