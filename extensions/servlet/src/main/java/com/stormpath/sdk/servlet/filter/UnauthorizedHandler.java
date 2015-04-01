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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Request handler for requests that require authorization (valid access control decision), but no authorization was
 * able to be obtained.
 *
 * @since 1.0.RC3
 */
public interface UnauthorizedHandler {

    /**
     * Returns {@code true} if the request should be able to continue through the filter chain to the final Servlet or
     * MVC Controller destination, {@code false} if the handler processed the request directly and filter chain
     * execution should stop immediately.
     *
     * @param request  inbound request
     * @param response outbound response
     * @return {@code true} if the request should be able to continue through the filter chain to the final Servlet or
     * MVC Controller destination, {@code false} if the handler processed the request directly and filter chain
     * execution should stop immediately.
     * @throws ServletException if there is an error.  An exception will also discontinue filter chain processing
     *                          immediately.
     */
    boolean onUnauthorized(HttpServletRequest request, HttpServletResponse response) throws ServletException;

}
