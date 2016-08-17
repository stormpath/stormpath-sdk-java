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
 * If the resolver returns a non-null/empty value, that
 * value is set as the header value.  If the resolver returns a null or empty String value, the header is not set.</p>
 *
 * @since 1.1.0
 */
public class AppliedRequestHeaderFilter extends ZuulFilter {

    private String type = "pre";

    private int order = 0;

    private String headerName;

    private Resolver<String> valueResolver;

    public void setFilterType(String type) {
        this.type = type;
    }

    @Override
    public String filterType() {
        return this.type;
    }

    public void setFilterOrder(int order) {
        this.order = order;
    }

    @Override
    public int filterOrder() {
        return this.order;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public Resolver<String> getValueResolver() {
        return valueResolver;
    }

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
