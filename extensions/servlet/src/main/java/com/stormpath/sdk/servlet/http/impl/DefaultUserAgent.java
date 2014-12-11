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
package com.stormpath.sdk.servlet.http.impl;

import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Objects;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.http.UserAgent;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultUserAgent implements UserAgent {

    private final HttpServletRequest request;

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
               ua.startsWith("Elinks ") || ua.startsWith("ELinks ") ||
               ua.startsWith("ELinks/") ||
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
    public boolean isHtmlPreferred() {

        List<AcceptedMediaType> mimeTypes = getMimeTypes();

        for(AcceptedMediaType mimeType : mimeTypes) {

            String val = mimeType.getName();

            if (val.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
                return false;
            }

            if (val.startsWith(MediaType.TEXT_HTML_VALUE) || val.startsWith(MediaType.APPLICATION_XHTML_XML_VALUE)) {
                return true;
            }
        }

        return false;
    }

    protected List<AcceptedMediaType> getMimeTypes() {

        String header = this.request.getHeader("Accept");

        if (Strings.hasText(header)) {

            String[] acceptValues = Strings.split(header);

            if (acceptValues != null && acceptValues.length > 0) {

                List<AcceptedMediaType> acceptedMediaTypes = new ArrayList<AcceptedMediaType>(acceptValues.length);

                for(int i = 0; i < acceptValues.length; i++) {

                    String mimeTypeString = acceptValues[i];

                    String mimeType = mimeTypeString;
                    double quality = 1d; //default per http spec

                    int j = mimeTypeString.indexOf(';');
                    if (j != -1) {

                        String paramsString = mimeTypeString.substring(j+1);
                        mimeType = mimeTypeString.substring(0, j);

                        String[] params = Strings.split(paramsString, ';');

                        if (params != null && params.length > 0) {

                            for (String param : params) {
                                if (param.startsWith("q=")) {
                                    param = param.substring(2);
                                    try {
                                        quality = Double.parseDouble(param);
                                    } catch (NumberFormatException e) {
                                        quality = 1d; //default
                                    }
                                }
                            }
                        }
                    }

                    AcceptedMediaType amt = new AcceptedMediaType(mimeType, quality, i);
                    acceptedMediaTypes.add(amt);
                }

                //sort according to the quality parameter, falling back to the list index when qualities are equal
                Collections.sort(acceptedMediaTypes);

                return acceptedMediaTypes;
            }
        }

        return Collections.emptyList();
    }

    protected class AcceptedMediaType implements Comparable<AcceptedMediaType> {

        private final String name;
        private final double quality;
        private final int listIndex;

        public AcceptedMediaType(String name, double quality, int listIndex) {
            Assert.hasText(name, "Name cannot be null or empty.");
            Assert.isTrue(listIndex >= 0, "list index must be zero or greater.");
            this.name = name;
            this.quality = Math.max(0, Math.min(1, quality));
            this.listIndex = listIndex;
        }

        public String getName() {
            return name;
        }

        public double getQuality() {
            return quality;
        }

        public int getListIndex() {
            return listIndex;
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(AcceptedMediaType acceptedMediaType) {
            if (acceptedMediaType == null) {
                return 1;
            }

            if (this.quality > acceptedMediaType.quality) {
                return -1; //higher qualities should be present earlier in the collection
            } else if (this.quality < acceptedMediaType.quality) {
                return 1; //lower qualities should be present earlier in the collection
            }

            //otherwise the quality is equal, so we need to fall back to the list index (the order that the media
            //type was declared in the header value)
            if (this.listIndex > acceptedMediaType.listIndex) {
                return 1;
            } else if (this.listIndex < acceptedMediaType.listIndex) {
                return -1;
            }

            //otherwise equal weight:
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;

            if (o instanceof AcceptedMediaType) {
                AcceptedMediaType a = (AcceptedMediaType)o;
                return this.name.equals(a.name) && this.quality == a.quality;
            }

            return false;
        }

        @Override
        public int hashCode() {
            int hashCode = this.name.hashCode();
            hashCode = 31 * hashCode + Objects.hashCode(this.quality);
            return hashCode;
        }

        @Override
        public String toString() {
            return name + ";q=" + quality;
        }
    }
}
