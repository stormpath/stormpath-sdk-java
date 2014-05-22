/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.oauth.http;

import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * OauthHttpServletRequest
 *
 * @since 1.0.RC
 */
public class OauthHttpServletRequest implements HttpServletRequest {

    private final HttpRequest httpRequest;

    public OauthHttpServletRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getAuthType() {
        throw new UnsupportedOperationException("getAuthType() method hasn't been implemented.");
    }

    @Override
    public Cookie[] getCookies() {
        throw new UnsupportedOperationException("getCookies() method hasn't been implemented.");
    }

    @Override
    public long getDateHeader(String name) {
        throw new UnsupportedOperationException("getDateHeader() method hasn't been implemented.");
    }

    @Override
    public String getHeader(String name) {
        for (Map.Entry<String, String[]> entry : httpRequest.getHeaders().entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue()[0];
            }
        }
        return null;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        for (Map.Entry<String, String[]> entry : httpRequest.getHeaders().entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return Collections.enumeration(Arrays.asList(entry.getValue()));
            }
        }
        return Collections.emptyEnumeration();
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(httpRequest.getHeaders().keySet());
    }

    @Override
    public int getIntHeader(String name) {
        throw new UnsupportedOperationException("getIntHeader() method hasn't been implemented.");
    }

    @Override
    public String getMethod() {
        return httpRequest.getMethod().name();
    }

    @Override
    public String getPathInfo() {
        throw new UnsupportedOperationException("getPathInfo() method hasn't been implemented.");
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException("getPathTranslated() method hasn't been implemented.");
    }

    @Override
    public String getContextPath() {
        throw new UnsupportedOperationException("getContextPath() method hasn't been implemented.");
    }

    @Override
    public String getQueryString() {
        return httpRequest.getQueryParameters();
    }

    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException("getRemoteUser() method hasn't been implemented.");
    }

    @Override
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException("isUserInRole() method hasn't been implemented.");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("getUserPrincipal() method hasn't been implemented.");
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("getRequestedSessionId() method hasn't been implemented.");
    }

    @Override
    public String getRequestURI() {
        throw new UnsupportedOperationException("getRequestURI() method hasn't been implemented.");
    }

    @Override
    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException("getRequestURL() method hasn't been implemented.");
    }

    @Override
    public String getServletPath() {
        throw new UnsupportedOperationException("getServletPath() method hasn't been implemented.");
    }

    @Override
    public HttpSession getSession(boolean create) {
        throw new UnsupportedOperationException("getSession() method hasn't been implemented.");
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException("getSession() method hasn't been implemented.");
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException("changeSessionId() method hasn't been implemented.");
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException("isRequestedSessionIdValid() method hasn't been implemented.");
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException("isRequestedSessionIdFromCookie() method hasn't been implemented.");
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException("isRequestedSessionIdFromURL() method hasn't been implemented.");
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException("isRequestedSessionIdFromUrl() method hasn't been implemented.");
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        throw new UnsupportedOperationException("authenticate() method hasn't been implemented.");
    }

    @Override
    public void login(String username, String password) throws ServletException {
        throw new UnsupportedOperationException("login() method hasn't been implemented.");
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException("logout() method hasn't been implemented.");
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        throw new UnsupportedOperationException("getParts() method hasn't been implemented.");
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        throw new UnsupportedOperationException("getPart() method hasn't been implemented.");
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        throw new UnsupportedOperationException("upgrade() method hasn't been implemented.");
    }

    @Override
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException("getAttribute() method hasn't been implemented.");
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException("getAttributeNames() method hasn't been implemented.");
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("getCharacterEncoding() method hasn't been implemented.");
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException("setCharacterEncoding() method hasn't been implemented.");
    }

    @Override
    public int getContentLength() {
        throw new UnsupportedOperationException("getContentLength() method hasn't been implemented.");
    }

    @Override
    public long getContentLengthLong() {
        throw new UnsupportedOperationException("getContentLengthLong() method hasn't been implemented.");
    }

    @Override
    public String getContentType() {
        return getHeader("Content-Type");
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("getInputStream() method hasn't been implemented.");
    }

    @Override
    public String getParameter(String name) {
        Assert.hasText(name);

        String[] parameterValue = httpRequest.getParameters().get(name);

        if (parameterValue == null || parameterValue.length == 0) {
            return null;
        }
        return parameterValue[0];
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(httpRequest.getParameters().keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        Assert.hasText(name);

        if (!httpRequest.getParameters().containsKey(name)) {
            return null;
        }
        return httpRequest.getParameters().get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return httpRequest.getParameters();
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException("getProtocol() method hasn't been implemented.");
    }

    @Override
    public String getScheme() {
        throw new UnsupportedOperationException("getScheme() method hasn't been implemented.");
    }

    @Override
    public String getServerName() {
        throw new UnsupportedOperationException("getServerName() method hasn't been implemented.");
    }

    @Override
    public int getServerPort() {
        throw new UnsupportedOperationException("getServerPort() method hasn't been implemented.");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException("getReader() method hasn't been implemented.");
    }

    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException("getRemoteAddr() method hasn't been implemented.");
    }

    @Override
    public String getRemoteHost() {
        throw new UnsupportedOperationException("getRemoteHost() method hasn't been implemented.");
    }

    @Override
    public void setAttribute(String name, Object o) {
        throw new UnsupportedOperationException("setAttribute() method hasn't been implemented.");
    }

    @Override
    public void removeAttribute(String name) {
        throw new UnsupportedOperationException("removeAttribute() method hasn't been implemented.");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("getLocale() method hasn't been implemented.");
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new UnsupportedOperationException("getLocales() method hasn't been implemented.");
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("isSecure() method hasn't been implemented.");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException("getRequestDispatcher() method hasn't been implemented.");
    }

    @Override
    public String getRealPath(String path) {
        throw new UnsupportedOperationException("getRealPath() method hasn't been implemented.");
    }

    @Override
    public int getRemotePort() {
        throw new UnsupportedOperationException("getRemotePort() method hasn't been implemented.");
    }

    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException("getLocalName() method hasn't been implemented.");
    }

    @Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException("getLocalAddr() method hasn't been implemented.");
    }

    @Override
    public int getLocalPort() {
        throw new UnsupportedOperationException("getLocalPort() method hasn't been implemented.");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("getServletContext() method hasn't been implemented.");
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException("startAsync() method hasn't been implemented.");
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        throw new UnsupportedOperationException("startAsync() method hasn't been implemented.");
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException("isAsyncStarted() method hasn't been implemented.");
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException("isAsyncStarted() method hasn't been implemented.");
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException("getAsyncContext() method hasn't been implemented.");
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException("getDispatcherType() method hasn't been implemented.");
    }
}
