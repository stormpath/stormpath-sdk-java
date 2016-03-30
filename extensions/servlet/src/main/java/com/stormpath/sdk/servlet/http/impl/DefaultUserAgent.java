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
package com.stormpath.sdk.servlet.http.impl;

import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.http.UserAgent;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.RC3
 */
public class DefaultUserAgent implements UserAgent {

    private static final String ACCEPT_HEADER_NAME = "Accept";

    private final HttpServletRequest request;

    private List<MediaType> acceptedMediaTypes; //cached value to eliminate multiple parse invocations
    private Boolean jsonPreferred; //cached value to eliminate multiple iterations
    private Boolean htmlPreferred; //cached value to eliminate multiple iterations

    public DefaultUserAgent(HttpServletRequest request) {
        Assert.notNull(request, "request argument cannot be null.");
        this.request = request;
    }

    @Override
    public boolean isBrowser() {
        String ua = request.getHeader("User-Agent");
        if (ua == null) {
            return false;
        }

        //logic copied from MIT-licensed code snippet here:
        //https://github.com/stuartpb/user-agent-is-browser/blob/master/lib/user-agent-is-browser.js

        // Check the prefix used by 99% of browsers
        return ua.startsWith("Mozilla/") ||

                // Older versions of Opera
                ua.startsWith("Opera/") ||

                // Down the rabbit hole...
                ua.startsWith("Lynx/") ||
                ua.startsWith("Links ") ||
                ua.startsWith("Elinks ") || ua.startsWith("ELinks ") || ua.startsWith("ELinks/") ||
                ua.startsWith("Midori/") ||
                ua.startsWith("w3m/") ||
                ua.startsWith("Webkit/") ||
                ua.startsWith("Vimprobable/") ||
                ua.startsWith("Dooble/") ||
                ua.startsWith("Dillo/") ||
                ua.startsWith("Surf/") ||
                ua.startsWith("NetSurf/") ||
                ua.startsWith("Galaxy/") ||
                ua.startsWith("Cyberdog/") ||
                ua.startsWith("iCab/") ||
                ua.startsWith("IBrowse/") ||
                ua.startsWith("IBM WebExplorer /") ||
                ua.startsWith("AmigaVoyager/") ||
                ua.startsWith("HotJava/") ||
                ua.startsWith("retawq/") ||
                ua.startsWith("uzbl ") || ua.startsWith("Uzbl ") ||
                ua.startsWith("NCSA Mosaic/") || ua.startsWith("NCSA_Mosaic/") ||
                // And, finally, we test to see if they're using *the first browser ever*.
                ua.startsWith("WorldWideweb (NEXT)");
    }

    @Override
    public List<MediaType> getAcceptedMediaTypes() {

        List<MediaType> accepted = this.acceptedMediaTypes;

        if (accepted == null) {

            //default to none:
            accepted = Collections.emptyList();

            //then reflect any header that might be present:
            String val = request.getHeader(ACCEPT_HEADER_NAME);
            val = Strings.clean(val);
            if (val != null) {
                accepted = MediaType.parseMediaTypes(val);
                MediaType.sortBySpecificityAndQuality(accepted);
            }

            this.acceptedMediaTypes = accepted;
        }

        return accepted;
    }

    @Override
    public boolean isHtmlPreferred() {
        if (htmlPreferred == null) {
            htmlPreferred = checkHtmlPreferred();
        }
        return htmlPreferred;
    }

    private boolean checkHtmlPreferred() {

        List<MediaType> mediaTypes = getAcceptedMediaTypes();

        for (MediaType mediaType : mediaTypes) {

            if (MediaType.APPLICATION_JSON.includes(mediaType)) {
                return false;
            }

            if (MediaType.TEXT_HTML.includes(mediaType) ||
                    MediaType.APPLICATION_XHTML_XML.includes(mediaType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isJsonPreferred() {
        if (jsonPreferred == null) {
            jsonPreferred = checkJsonPreferred();
        }
        return jsonPreferred;
    }

    private boolean checkJsonPreferred() {

        List<MediaType> mediaTypes = getAcceptedMediaTypes();

        for (MediaType mediaType : mediaTypes) {

            if (MediaType.APPLICATION_JSON.includes(mediaType)) {
                return true;
            }

            if (MediaType.TEXT_HTML.includes(mediaType) ||
                    MediaType.APPLICATION_XHTML_XML.includes(mediaType)) {
                return false;
            }
        }

        return false;
    }
}
