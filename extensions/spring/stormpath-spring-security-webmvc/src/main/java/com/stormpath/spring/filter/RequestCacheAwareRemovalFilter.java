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

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * See https://github.com/stormpath/stormpath-sdk-java/issues/718
 *
 * @since 1.0.0
 */
public class RequestCacheAwareRemovalFilter extends GenericFilterBean {

    private RequestCache requestCache;

    private List<String> uriWhiteList;

    public RequestCacheAwareRemovalFilter(List<String> urlWhileList) {
        this(urlWhileList, new HttpSessionRequestCache());
    }

    public RequestCacheAwareRemovalFilter(List<String> uriWhileList, RequestCache requestCache) {
        Assert.notNull(uriWhileList, "urlWhileList cannot be null");
        Assert.notNull(requestCache, "requestCache cannot be null");
        this.requestCache = requestCache;
        this.uriWhiteList = uriWhileList;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest wrappedSavedRequest = requestCache.getMatchingRequest(
                (HttpServletRequest) request, (HttpServletResponse) response);

        if (wrappedSavedRequest == null && !isUriWhitelisted(uriWhiteList, ((HttpServletRequest) request).getRequestURI())) {
            requestCache.removeRequest((HttpServletRequest) request, (HttpServletResponse) response);
        }

        chain.doFilter(wrappedSavedRequest == null ? request : wrappedSavedRequest,
                response);
    }

    protected boolean isUriWhitelisted(List<String> urlWhiteList, String requestUrl) {
        for (String url : urlWhiteList) {
            if (requestUrl.contains(url)) {
                return true;
            }
        }
        return false;
    }


}
