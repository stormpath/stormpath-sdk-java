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

import javax.servlet.http.HttpServletRequest;

public class DefaultServerUriResolver implements ServerUriResolver {

    @Override
    public String getServerUri(HttpServletRequest request) {

        String scheme = request.getScheme();

        String serverName = request.getServerName();

        int port = request.getServerPort();

        boolean includePort = true;

        if ((scheme.equalsIgnoreCase("http") && port == 80) ||
            (scheme.equalsIgnoreCase("https") && port == 443)) {
            includePort = false;
        }

        String uri = scheme + "://" + serverName;
        if (includePort) {
            uri += ":" + port;
        }

        return uri;
    }
}
