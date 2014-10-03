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
package com.stormpath.sdk.servlet.authc;

/**
 * Parses an Authorization header value according to the
 * <a href="https://tools.ietf.org/html/rfc7235#appendix-C">HTTP Authentication grammar</a> specification.
 */
public class DefaultAuthorizationHeaderParser implements AuthorizationHeaderParser {

    @Override
    public HttpCredentials parse(String headerValue) {

        if (headerValue == null) {
            return null;
        }

        String schemeName = null;
        String schemeValue = null;

        StringBuilder sb = new StringBuilder();

        boolean schemeDiscovered = false;

        for (int i = 0; i < headerValue.length(); i++) {

            char c = headerValue.charAt(i);

            if (!Character.isWhitespace(c)) {
                sb.append(c);
                if (!schemeDiscovered) { //we're seeing the first text so far, so it must be the scheme name:
                    schemeDiscovered = true;
                }
            } else {
                if (schemeDiscovered) {
                    if (schemeName == null) {
                        //we're seeing whitespace after the scheme name, so we need to finalize the scheme name
                        //and then reset the buffer to start accepting the upcoming scheme value:
                        schemeName = sb.toString(); //convert existing buffer to the schemeName;
                        sb = new StringBuilder(); //reset the buffer for the scheme value:
                    } else {
                        //we're in the value segment - append the discovered character:
                        sb.append(c);
                    }
                }
            }
        }

        String val = sb.length() > 0 ? sb.toString() : null;

        if (schemeName == null) {
            schemeName = val;
        } else {
            schemeValue = val;
        }

        if (schemeValue != null) {
            schemeValue = schemeValue.trim(); //trim any trailing whitespace if it exists
        }

        return new DefaultHttpCredentials(schemeName, schemeValue);
    }
}
