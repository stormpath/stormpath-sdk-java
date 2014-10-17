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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.util.AntPathMatcher;
import com.stormpath.sdk.servlet.util.PatternMatcher;
import com.stormpath.sdk.servlet.util.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PathMatchingFilterChainResolver implements FilterChainResolver {

    private static transient final Logger log = LoggerFactory.getLogger(PathMatchingFilterChainResolver.class);

    private FilterChainManager filterChainManager;

    private PatternMatcher pathMatcher;

    public PathMatchingFilterChainResolver(ServletContext servletContext) throws ServletException {
        this.pathMatcher = new AntPathMatcher();
        this.filterChainManager = new DefaultFilterChainManager(servletContext);
    }

    /**
     * Returns the {@code PatternMatcher} used when determining if an incoming request's path
     * matches a configured filter chain.  Unless overridden, the
     * default implementation is an {@link AntPathMatcher AntPathMatcher}.
     *
     * @return the {@code PatternMatcher} used when determining if an incoming request's path
     *         matches a configured filter chain.
     */
    public PatternMatcher getPathMatcher() {
        return pathMatcher;
    }

    /**
     * Sets the {@code PatternMatcher} used when determining if an incoming request's path
     * matches a configured filter chain.  Unless overridden, the
     * default implementation is an {@link AntPathMatcher AntPathMatcher}.
     *
     * @param pathMatcher the {@code PatternMatcher} used when determining if an incoming request's path
     *                    matches a configured filter chain.
     */
    public void setPathMatcher(PatternMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public FilterChainManager getFilterChainManager() {
        return filterChainManager;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setFilterChainManager(FilterChainManager filterChainManager) {
        this.filterChainManager = filterChainManager;
    }

    public FilterChain getChain(HttpServletRequest request, HttpServletResponse response, FilterChain originalChain) {

        FilterChainManager filterChainManager = getFilterChainManager();
        if (!filterChainManager.hasChains()) {
            return originalChain;
        }

        String requestUri = getContextRelativeUri(request);

        //the 'chain names' in this implementation are actually path patterns defined by the user.  We just use them
        //as the chain name for the FilterChainManager's requirements
        for (String pathPattern : filterChainManager.getChainNames()) {

            // If the path does match, then pass on to the subclass implementation for specific checks:
            if (pathMatches(pathPattern, requestUri)) {
                if (log.isTraceEnabled()) {
                    log.trace("Matched path pattern [" + pathPattern + "] for requestUri [" + requestUri + "].  " +
                              "Utilizing corresponding filter chain...");
                }
                return filterChainManager.proxy(originalChain, pathPattern);
            }
        }

        return originalChain;
    }

    /**
     * Returns {@code true} if an incoming request path (the {@code path} argument)
     * matches a configured filter chain path (the {@code pattern} argument), {@code false} otherwise.
     * <p/>
     * Simply delegates to
     * <b><code>{@link #getPathMatcher() getPathMatcher()}.{@link PatternMatcher#matches(String, String) matches(pattern,path)}</code></b>.
     * Subclass implementors should think carefully before overriding this method, as typically a custom
     * {@code PathMatcher} should be configured for custom path matching behavior instead.  Favor OO composition
     * rather than inheritance to limit your exposure to Shiro implementation details which may change over time.
     *
     * @param pattern the pattern to match against
     * @param path    the value to match with the specified {@code pattern}
     * @return {@code true} if the request {@code path} matches the specified filter chain url {@code pattern},
     *         {@code false} otherwise.
     */
    protected boolean pathMatches(String pattern, String path) {
        PatternMatcher pathMatcher = getPathMatcher();
        return pathMatcher.matches(pattern, path);
    }

    /**
     * Returns the context path within the application based on the specified <code>request</code>.
     * <p/>
     * This implementation merely delegates to
     * {@link com.stormpath.sdk.servlet.util.ServletUtils#getContextRelativeUri(javax.servlet.http.HttpServletRequest)
     * ServletUtils.getContextRelativePath(request)},
     * but can be overridden by subclasses for custom logic.
     *
     * @param request the incoming <code>ServletRequest</code>
     * @return the context path within the application.
     */
    protected String getContextRelativeUri(HttpServletRequest request) {
        return ServletUtils.getContextRelativeUri(request);
    }
}
