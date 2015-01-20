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

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC3
 */
public class DefaultServerUriResolver implements ServerUriResolver {

    private boolean excludeSchemeDefaultPorts = true;

    public boolean isExcludeSchemeDefaultPorts() {
        return excludeSchemeDefaultPorts;
    }

    public void setExcludeSchemeDefaultPorts(boolean excludeSchemeDefaultPorts) {
        this.excludeSchemeDefaultPorts = excludeSchemeDefaultPorts;
    }

    /**
     * This will return:
     * <ol>
     *     <li>The http scheme such as {@code http} or {@code https}</li>
     *     <li>The scheme separator {@code ://}</li>
     *     <li>The server host name as specified in the {@code Host} header.</li>
     *     <li>If the server port is specified in the {@code Host} header and the
     *         port is a non-standard port for the specified http scheme:
     *         <ol>
     *             <li>colon character {@code :}</li>
     *             <li>server port</li>
     *         </ol>
     *     </li>
     * </ol>
     * @param request http servlet request
     * @return the root/base server URI for the specified request.
     */
    @Override
    public String getServerUri(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder();

        String scheme = request.getScheme();
        sb.append(scheme).append("://");

        String host = request.getHeader("Host");
        if (host == null) { //HTTP 1.0?
            host = request.getServerName();
        }
        String port = null;

        int i = host.lastIndexOf(':');
        if (i > -1) {
            port = host.substring(i+1);
            host = host.substring(0, i);
        }

        sb.append(host);

        boolean includePort = port != null;

        if (includePort && isExcludeSchemeDefaultPorts() &&
            ((scheme.equalsIgnoreCase("http") && "80".equals(port)) ||
            (scheme.equalsIgnoreCase("https") && "443".equals(port)))) {
            includePort = false;
        }

        if (includePort) {
            sb.append(':').append(port);
        }

        return sb.toString();
    }
}
