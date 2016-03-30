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
package com.stormpath.sdk.servlet.http;

import com.stormpath.sdk.servlet.http.impl.DefaultUserAgent;

import javax.servlet.http.HttpServletRequest;

/**
 * Date: 3/28/16
 */
public class UserAgents {

    public static final String USER_AGENT_REQUEST_ATTRIBUTE_NAME = UserAgents.class.getName() + ".USER_AGENT";

    public static UserAgent get(HttpServletRequest request) {
        DefaultUserAgent ua = (DefaultUserAgent) request.getAttribute(USER_AGENT_REQUEST_ATTRIBUTE_NAME);
        if (ua == null) { //cache for potential later access:
            ua = new DefaultUserAgent(request);
            request.setAttribute(USER_AGENT_REQUEST_ATTRIBUTE_NAME, ua);
        }
        return ua;
    }
}
