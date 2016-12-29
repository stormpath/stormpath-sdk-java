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
package com.stormpath.spring.filter;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.http.HttpHeadersHolder;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.filter.HttpFilter;
import com.stormpath.sdk.servlet.filter.WrappedServletRequestFactory;

import javax.servlet.FilterChain;
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
 * This filter adds Client and Application as attributes to every request in order for subsequent Filters to have access to them.
 * For example, a filter trying to validate an access token will need to have access to the Application (see AuthorizationHeaderAccountResolver)
 *
 * @since 1.3.0
 */
public class StormpathWrapperFilter extends HttpFilter {

    protected Client client;

    protected Application application;

    protected Set<String> clientRequestAttributeNameList;

    protected Set<String> applicationRequestAttributeNameList;

    protected WrappedServletRequestFactory wrappedServletRequestFactory;

    public StormpathWrapperFilter() {
    }

    public void setClientRequestAttributeNames(Set<String> clientRequestAttributeNames) {
        this.clientRequestAttributeNameList =
                clientRequestAttributeNames != null ? clientRequestAttributeNames : new LinkedHashSet<String>();
    }

    public void setApplicationRequestAttributeNames(Set<String> applicationRequestAttributeNames) {
        this.applicationRequestAttributeNameList =
                applicationRequestAttributeNames != null ? applicationRequestAttributeNames : new LinkedHashSet<String>();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @Override
    public void filter(HttpServletRequest request, HttpServletResponse response, final FilterChain chain)
            throws Exception {

        setRequestAttributes(request);

        //wrap:
        request = wrapRequest(request, response);

        //continue:
        chain.doFilter(request, response);

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

    public void setWrappedServletRequestFactory(WrappedServletRequestFactory factory) {
        Assert.notNull(factory, "WrappedServletRequestFactory cannot be null.");
        this.wrappedServletRequestFactory = factory;
    }

    protected void setClientRequestAttributes(HttpServletRequest request) {
        String name = Client.class.getName();
        //value must always be set:
        request.setAttribute(name, client);

        //user customized values:
        for (String aName : clientRequestAttributeNameList) {
            request.setAttribute(aName, client);
        }
    }

    protected void setApplicationRequestAttributes(HttpServletRequest request) {
        String name = Application.class.getName();
        //this must always be set:
        request.setAttribute(name, application);

        //user-customized values:
        for (String aName : applicationRequestAttributeNameList) {
            request.setAttribute(aName, application);
        }
    }

    protected HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.wrappedServletRequestFactory.wrapHttpServletRequest(request, response);
    }

}
