/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Zuul filter that obtains a String value via a <code>Resolver&lt;String&gt;</code> and sets the
 * value as header on the intercepted/outgoing Request.  The header name is configurable via
 * {@link #setHeaderName(String) setHeaderName}.
 * <p>The <code>Resolver&lt;String&gt;</code> may be set via {@link #setValueResolver(Resolver) setValueResolver}.
 * If the resolver returns a non-null/non-empty value, that
 * value is set as the header value.  If the resolver returns a null or empty String value, the header is not set.</p>
 *
 * @see #setValueResolver(Resolver)
 * @see #setHeaderName(String)
 * @see #setFilterOrder(int)
 * @since 1.1.0
 */
public class AppliedRequestHeaderFilter extends ZuulFilter {

    private String type = "pre";

    private int order = 0;

    private String headerName;

    private Resolver<String> valueResolver;

    /**
     * Sets the filter type.  Default value is {@code pre} and probably shouldn't be overridden since request
     * headers must be set in Zuul's {@code pre} phase.
     *
     * @param type the filter type.
     */
    public void setFilterType(String type) {
        this.type = type;
    }

    @Override
    public String filterType() {
        return this.type;
    }

    /**
     * Sets the execution order of the filter.  This value is returned from {@link #filterOrder()}.
     *
     * @param order the execution order of the filter.
     */
    public void setFilterOrder(int order) {
        this.order = order;
    }

    @Override
    public int filterOrder() {
        return this.order;
    }

    /**
     * Returns the name of the header to set if a value is returned from the
     * {@link #setValueResolver(Resolver) valueResolver}. If no value is resolved, the header will not be set.
     *
     * @return the name of the header to set if a value is returned from the {@code valueResolver}.
     */
    public String getHeaderName() {
        return headerName;
    }

    /**
     * The name of the header to set if a value is returned from the {@link #setValueResolver(Resolver) valueResolver}.
     * If no value is resolved, the header will not be set.
     *
     * @param headerName name of the header to set if a value is returned from the
     *                   {@link #setValueResolver(Resolver) valueResolver}.
     */
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    /**
     * Returns the resolver to use to acquire a header value. If the resolver returns a value, the header will be set.
     * If the resolver returns null or the empty string, the header will not be set at all.
     *
     * @return the resolver to use to acquire a header value.
     */
    public Resolver<String> getValueResolver() {
        return valueResolver;
    }

    /**
     * Sets the resolver to use to acquire a header value.  If the resolver returns a value, the header will be set.
     * If the resolver returns null or the empty string, the header will not be set at all.
     *
     * @param valueResolver the resolver to use to acquire a header value.
     */
    public void setValueResolver(Resolver<String> valueResolver) {
        this.valueResolver = valueResolver;
    }

    @Override
    public boolean shouldFilter() {
        return getValueResolver() != null;
    }

    @SuppressWarnings("WeakerAccess") //needs to be protected for tests and child class overrides.
    protected String getHeaderValue() {
        RequestContext ctx = getRequestContext();
        return getHeaderValue(ctx.getRequest(), ctx.getResponse());
    }

    @SuppressWarnings("WeakerAccess") //needs to be protected for tests and child class overrides.
    protected String getHeaderValue(HttpServletRequest request, HttpServletResponse response) {
        Resolver<String> valueResolver = getValueResolver();
        return valueResolver.get(request, response);
    }

    @Override
    public Object run() {
        String value = getHeaderValue();
        if (Strings.hasText(value)) {
            String headerName = getHeaderName();
            getRequestContext().addZuulRequestHeader(headerName, value);
        }
        return null;
    }

    @SuppressWarnings("WeakerAccess") //needs to be protected for tests and child class overrides.
    protected RequestContext getRequestContext() {
        return RequestContext.getCurrentContext();
    }
}
