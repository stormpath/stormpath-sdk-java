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
package com.stormpath.sdk.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple MVC Controller concept used by Stormpath components.  This allows Stormpath to work in any MVC environment
 * (Servlet, Spring MVC, etc) by using its own concept - a simple adapter just needs to be created for each actual
 * runtime environment.
 *
 * <p>This ensures that Stormpath request processing and view rendering logic is identical (and testable/verifiable)
 * across any/all supported MVC runtime environments where Stormpath may be deployed.</p>
 *
 * @since 1.0.RC4
 */
public interface Controller {
    String NEXT_QUERY_PARAM = "next";

    String STORMPATH_JSON_VIEW_NAME = "stormpathJsonView";

    /**
     * Processes a view request (either to render or handle submission) and returns a {@link ViewModel} that represents
     * the resulting view to be rendered.  A {@code null} return value indicates that the controller implementation
     * handled the response and committed to it directly (setting status codes, writing directly to the response output
     * stream), etc.
     *
     * @param request  inbound request.
     * @param response outbound response.
     * @return a View the view to be rendered or {@code null} if the response was handled (and committed) directly.
     * @throws Exception
     */
    ViewModel handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
