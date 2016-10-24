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

import com.stormpath.sdk.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter base class that guarantees to be just executed once per request, on any servlet container. It provides a
 * {@link #filter} method with HttpServletRequest and HttpServletResponse arguments.  Can also be enabled/disabled
 * at runtime via the {@link #isEnabled(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
 * implementation that can be overridden by subclasses.
 * <p>
 * <p>The {@link #getFilteredAttributeName} method determines how to identify that a request is already
 * filtered. The default implementation is based on the configured name of the concrete filter instance.</p>
 * <p>
 * This class was initially borrowed from the Apache Shiro and Spring Framework projects with additional
 * modifications.
 *
 * @since 1.0.RC3
 */
public abstract class HttpFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(HttpFilter.class);

    /**
     * Suffix that gets appended to the filter name for the "already filtered" request attribute: {@code .FILTERED}
     *
     * @see #getFilteredAttributeName
     */
    public static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";

    private FilterConfig filterConfig;
    private ServletContext servletContext;
    private String name;

    /**
     * Determines generally if this filter should execute or let requests fall through to the next chain element.
     *
     * @see #isEnabled()
     */
    private boolean enabled = true; //most filters wish to execute when configured, so default to true

    /**
     * Returns {@code true} if this filter should <em>generally</em><b>*</b> execute for any request,
     * {@code false} if it should let the request/response pass through immediately to the next
     * element in the {@link javax.servlet.FilterChain}.  The default value is {@code true}, as most filters would
     * inherently need to execute when configured.
     * <p>
     * <p>If this value is set to {@code true}, the filter implementation can make a request-specific decision by
     * overriding the {@link #isEnabled(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
     * method.</p>
     * <p>
     * <p>If this value is {@code false}, the filter will not be executed and next element in the filter chain will
     * be executed immediately.</p>
     *
     * @return {@code true} if this filter should <em>generally</em> execute, {@code false} if it should let the
     * request/response pass through immediately to the next element in the {@link javax.servlet.FilterChain}.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether or not this filter <em>generally</em> executes for any request.  See the
     * {@link #isEnabled() isEnabled()} JavaDoc as to what <em>general</em> execution means.
     *
     * @param enabled whether or not this filter <em>generally</em> executes.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public final void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.servletContext = filterConfig.getServletContext();
        this.name = filterConfig.getFilterName();
        try {
            onInit();
        } catch (Exception e) {
            String msg = "Unable to initialize '" + name + "' filter of type " + getClass().getName() + ": " + e.getMessage();
            throw new ServletException(msg, e);
        }
    }

    protected void onInit() throws Exception {
        //no op, can be overridden by subclasses
    }

    /**
     * Returns {@code true} if this filter should filter the specified request, {@code false} if it should let the
     * request/response pass through immediately to the next element in the {@code FilterChain}.
     * <p>
     * <p>This default implementation merely returns the value of {@link #isEnabled() isEnabled()}, which is
     * {@code true} by default (to ensure the filter always executes by default), but it can be overridden by
     * subclasses for request-specific behavior if necessary.  For example, a filter could be enabled or disabled
     * based on the request path being accessed.</p>
     *
     * @param request  the incoming servlet request
     * @param response the outbound servlet response
     * @return {@code true} if this filter should filter the specified request, {@code false} if it should let the
     * request/response pass through immediately to the next element in the {@code FilterChain}.
     * @throws Exception in the case of any error
     */
    @SuppressWarnings({"UnusedParameters"})
    protected boolean isEnabled(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return true;
    }

    /**
     * Returns {@code true} if this filter should allow the filter chain to continue to execute, {@code false} if the
     * filter will commit and/or render the response directly.  This method is only invoked if
     * {@link #isEnabled(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)} is
     * {@code true}.
     * <p>
     * <p>This default implementation always returns {@code true} and can be overridden by subclasses for custom
     * logic.</p>
     *
     * @param request  incoming request
     * @param response outbound response
     * @return {@code true} if this filter should allow the filter chain to continue to execute, {@code false} if the
     * filter will commit and/or render the response directly.
     * @throws Exception in the case of an error
     */
    protected boolean isContinue(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return true;
    }

    /**
     * Return name of the request attribute that identifies that a request has already been filtered.
     * <p>
     * <p>The default implementation takes the configured {@link #getName() name} and appends
     * &quot;{@code .FILTERED}&quot;.  If the filter is not fully initialized, it falls back to the implementation's
     * class name.</p>
     *
     * @return the name of the request attribute that identifies that a request has already been filtered.
     * @see #getName
     * @see #ALREADY_FILTERED_SUFFIX
     */
    protected String getFilteredAttributeName() {
        String name = getName();
        if (name == null) {
            name = getClass().getName();
        }
        return name + ALREADY_FILTERED_SUFFIX;
    }

    /**
     * This {@code doFilter} implementation stores a request attribute for
     * "already filtered", proceeding without filtering again if the
     * attribute is already there.
     *
     * @see #getFilteredAttributeName
     * @see #filter
     */
    public final void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
        throws ServletException, IOException {

        Assert.isInstanceOf(HttpServletRequest.class, req, "Only HTTP requests are supported.");
        Assert.isInstanceOf(HttpServletResponse.class, resp, "Only HTTP responses are supported.");

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String alreadyFilteredAttributeName = getFilteredAttributeName();

        if (request.getAttribute(alreadyFilteredAttributeName) != null) {
            log.trace("Filter '{}' already executed.  Proceeding without invoking this filter.", getName());
            chain.doFilter(request, response);
        } else {
            //invoke this filter
            log.trace("Filter '{}' not yet executed.  Executing now.", getName());
            request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);

            try {
                if (isEnabled() && isEnabled(request, response)) {
                    if (isContinue(request, response)) {
                        log.trace("Filter '{}' is enabled and will filter the request.", this.name);
                        filter(request, response, chain);
                    } else {
                        //else the filter chain should not continue because isContinue rendered the response directly
                        String msg = "Filter '{}' has stopped chain execution and rendered the response directly.";
                        log.debug(msg, this.name);
                    }
                } else {
                    log.debug("Filter '{}' is not enabled. Continuing filter chain immediately.", this.name);
                    chain.doFilter(request, response);
                }
            } catch (IOException | ServletException | RuntimeException ioe) {
                throw ioe;
            } catch (Exception e) {
                String msg = "Filtered request resulted in an exception: " + e.getMessage();
                throw new ServletException(msg, e);
            } finally {
                // Once the request has finished, we're done and we don't
                // need to mark as 'already filtered' any more.
                request.removeAttribute(alreadyFilteredAttributeName);
            }
        }
    }

    /**
     * Same contract as for
     * {@link #doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)},
     * but guaranteed to be invoked only once per request.  Default implementation simply invokes
     * {@code chain.doFilter(request, response)}.
     *
     * @param request  incoming {@code ServletRequest}
     * @param response outgoing {@code ServletResponse}
     * @param chain    the {@code FilterChain} to execute
     * @throws Exception if there is a problem processing the request
     */
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        //no-op, can be overridden by subclasses
    }
}
