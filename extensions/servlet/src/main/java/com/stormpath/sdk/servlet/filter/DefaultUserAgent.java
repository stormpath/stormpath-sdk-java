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

import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class DefaultUserAgent implements UserAgent {

    private final HttpServletRequest request;

    public DefaultUserAgent(HttpServletRequest request) {
        Assert.notNull(request, "request argument cannot be null.");
        this.request = request;
    }

    @Override
    public boolean isRestClient() {

        Enumeration<String> acceptValues = request.getHeaders("Accept");

        if (acceptValues != null) {

            while (acceptValues.hasMoreElements()) {
                String acceptedContentType = acceptValues.nextElement();

                if (acceptedContentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
                    return true;
                }

                if (acceptedContentType.startsWith(MediaType.TEXT_HTML_VALUE) ||
                    acceptedContentType.startsWith(MediaType.APPLICATION_XHTML_XML_VALUE)) {
                    return false;
                }
            }
        }

        return false;
    }
}
