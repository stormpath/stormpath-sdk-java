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

public interface ServerUriResolver {

    public static final ServerUriResolver INSTANCE = new DefaultServerUriResolver();

    /**
     * Returns the root/base server URI for the specified request.  This will return:
     * <ol>
     *     <li>The http scheme such as {@code http} or {@code https}</li>
     *     <li>The scheme separator {@code ://}</li>
     *     <li>The server host name as represented by
     *         {@link javax.servlet.http.HttpServletRequest#getServerName()}</li>
     *     <li>If the server port is non-standard for the above scheme:
     *         <ol>
     *             <li>colon character {@code :}</li>
     *             <li>server port</li>
     *         </ol>
     *     </li>
     * </ol>
     * @param request http servlet request
     * @return the root/base server URI for the specified request.
     */
    String getServerUri(HttpServletRequest request);

}
