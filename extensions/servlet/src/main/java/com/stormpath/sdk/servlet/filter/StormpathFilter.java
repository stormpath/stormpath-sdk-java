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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.http.HttpHeadersHolder;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @since 1.0.RC3
 */
public class StormpathFilter extends HttpFilter {

    private FilterChainResolver filterChainResolver;
    private Set<String> clientRequestAttributeNames;
    private Set<String> applicationRequestAttributeNames;
    private WrappedServletRequestFactory factory;
    private Client client;
    private Application application;

    public StormpathFilter() {
        this.clientRequestAttributeNames = Collections.emptySet();
        this.applicationRequestAttributeNames = Collections.emptySet();
    }

    public void setFilterChainResolver(FilterChainResolver filterChainResolver) {
        Assert.notNull(filterChainResolver, "FilterChainResolver cannot be null.");
        this.filterChainResolver = filterChainResolver;
    }

    public void setClientRequestAttributeNames(Set<String> clientRequestAttributeNames) {
        this.clientRequestAttributeNames =
            clientRequestAttributeNames != null ? clientRequestAttributeNames : new LinkedHashSet<String>();
    }

    public void setApplicationRequestAttributeNames(Set<String> applicationRequestAttributeNames) {
        this.applicationRequestAttributeNames =
            applicationRequestAttributeNames != null ? applicationRequestAttributeNames : new LinkedHashSet<String>();
    }

    public void setWrappedServletRequestFactory(WrappedServletRequestFactory factory) {
        Assert.notNull(factory, "WrappedServletRequestFactory cannot be null.");
        this.factory = factory;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @Override
    protected void onInit() throws ServletException {
        Assert.notNull(filterChainResolver, "FilterChainResolver cannot be null.");
        Assert.notNull(clientRequestAttributeNames, "clientRequestAttributeNames cannot be null.");
        Assert.notNull(applicationRequestAttributeNames, "applicationRequestAttributeNames cannot be null.");
        Assert.notNull(factory, "WrappedServletRequestFactory cannot be null.");
        Assert.notNull(client, "Client instance cannot be null.");
        Assert.notNull(application, "Application instance cannot be null.");
    }

    protected FilterChainResolver getFilterChainResolver() {
        return this.filterChainResolver;
    }

    @Override
    public void filter(HttpServletRequest request, HttpServletResponse response, final FilterChain chain)
        throws Exception {

        FilterChainResolver resolver = getFilterChainResolver();
        Assert.notNull(resolver, "Filter has not yet been configured. Explicitly call setFilterChainResolver or " +
                "init(FilterConfig).");

        setRequestAttributes(request);

        //wrap:
        request = wrapRequest(request, response);

        FilterChain target = resolver.getChain(request, response, chain);

        //continue:
        target.doFilter(request, response);

        HttpHeadersHolder.clear();
    }

    protected void setRequestAttributes(HttpServletRequest request) {
        //ensure the Client and Application are conveniently available to all request filters/handlers:
        setClientRequestAttributes(request);
        setApplicationRequestAttributes(request);

        // set client headers on a thread local so they can be retrieved in DefaultDataStore
        Map<String, List<String>> headersMap = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            // Tomcat returns all header names as lowercase. In case others don't, lowercase key name
            // http://grokbase.com/t/tomcat/users/0968njb9en/header-names-lower-case
            headersMap.put(name.toLowerCase(), Collections.list(request.getHeaders(name)));
        }

        HttpHeadersHolder.set(headersMap);
    }

    protected void setClientRequestAttributes(HttpServletRequest request) {
        String name = Client.class.getName();
        //value must always be set:
        request.setAttribute(name, client);

        //user customized values:
        for (String aName : clientRequestAttributeNames) {
            request.setAttribute(aName, client);
        }
    }

    protected void setApplicationRequestAttributes(HttpServletRequest request) {
        String name = Application.class.getName();
        //this must always be set:
        request.setAttribute(name, application);

        //user-customized values:
        for (String aName : applicationRequestAttributeNames) {
            request.setAttribute(aName, application);
        }
    }

    protected HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.factory.wrapHttpServletRequest(request, response);
    }
}
