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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class PathMatchingFilter extends HttpFilter {

    /**
     * Log available to this class only
     */
    private static final Logger log = LoggerFactory.getLogger(PathMatchingFilter.class);

    /**
     * PatternMatcher used in determining which paths to react to for a given request.
     */
    protected PatternMatcher pathMatcher = new AntPathMatcher();

    /**
     * The context-relative path patterns that indicate when this filter should execute.  When an inbound request has
     * a URI that matches one of these patterns, the filter will execute.  When a request does not match, the filter
     * will not execute (and let the request flow through unimpeded to the next filter in the chain).
     */
    protected Set<String> pathPatterns = new LinkedHashSet<String>();

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

    /**
     * Returns <code>true</code> if the incoming <code>request</code> matches the specified <code>path</code> pattern,
     * <code>false</code> otherwise.
     * <p/>
     * The default implementation acquires the <code>request</code>'s context-relative URI within the application and
     * determines if that matches:
     * <p/>
     * <code>String relativeUri = {@link #getContextRelativeUri(javax.servlet.http.HttpServletRequest)};<br/>
     * return {@link #pathsMatch(String, String) pathsMatch(path,relativeUri)}</code>
     *
     * @param pathPattern the configured url pattern to check the incoming request against.
     * @param request     the incoming ServletRequest
     * @return <code>true</code> if the incoming <code>request</code> matches the specified <code>path</code> pattern,
     * <code>false</code> otherwise.
     */
    protected boolean pathsMatch(String pathPattern, HttpServletRequest request) {
        String requestUri = getContextRelativeUri(request);
        log.trace("Attempting to match pattern '{}' with current requestUri '{}'...", pathPattern, requestUri);
        return pathsMatch(pathPattern, requestUri);
    }

    /**
     * Returns <code>true</code> if the <code>path</code> matches the specified <code>pattern</code> string,
     * <code>false</code> otherwise.
     *
     * <p>Simply delegates to
     * <b><code>this.pathMatcher.{@link PatternMatcher#matches(String, String) matches(pattern,path)}</code></b>,
     * but can be overridden by subclasses for custom matching behavior.</p>
     *
     * @param pattern the pattern to match against
     * @param path    the value to match with the specified <code>pattern</code>
     * @return <code>true</code> if the <code>path</code> matches the specified <code>pattern</code> string,
     * <code>false</code> otherwise.
     */
    protected boolean pathsMatch(String pattern, String path) {
        return pathMatcher.matches(pattern, path);
    }

    /**
     * Returns {@code true} if the request URI matches one of the filters' configured path patterns, {@code false} if
     * the filter chain should continue immediately.
     *
     * @param request  the incoming servlet request
     * @param response the outbound servlet response
     * @return {@code true} if the request URI matches one of the filters' configured path patterns, {@code false} if
     * the filter chain should continue immediately.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected boolean isEnabled(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        for (String pattern : this.pathPatterns) {
            // If the pattern does match, then pass on to the subclass implementation for specific checks
            //(first match 'wins'):
            if (pathsMatch(pattern, request)) {
                log.trace("Current requestUri matches pattern '{}'.  Determining filter chain execution...", pattern);
                return isMatchedRequestEnabled(request, response, pattern);
            }
        }

        //pattern did not match, no need to execute - just let the chain continue:
        return false;
    }

    protected boolean isMatchedRequestEnabled(HttpServletRequest request, HttpServletResponse response,
                                              String matchedPattern) {
        return true;
    }
}
