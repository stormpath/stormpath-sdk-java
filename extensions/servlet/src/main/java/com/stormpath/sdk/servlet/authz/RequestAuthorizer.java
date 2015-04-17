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
package com.stormpath.sdk.servlet.authz;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Asserts that a request is authorized to be executed, and if not, throws an exception to indicate the filter chain
 * should not continue.
 *
 * @since 1.0.RC3
 */
public interface RequestAuthorizer {

    /**
     * Asserts that a request is authorized to be executed, and if not, throws an exception to indicate the filter chain
     * should not continue.
     *
     * @param request  inbound request
     * @param response outbound response
     */
    void assertAuthorized(HttpServletRequest request, HttpServletResponse response);
}
