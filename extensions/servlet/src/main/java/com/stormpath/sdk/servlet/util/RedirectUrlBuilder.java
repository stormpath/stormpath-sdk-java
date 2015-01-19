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
package com.stormpath.sdk.servlet.util;

import com.stormpath.sdk.lang.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @since 1.0.RC3
 */
public class RedirectUrlBuilder {

    /**
     * The default encoding scheme: UTF-8
     */
    public static final String DEFAULT_ENCODING_SCHEME = "UTF-8";

    private String url;

    private boolean contextRelative = false;

    private String encodingScheme = DEFAULT_ENCODING_SCHEME;

    private Map<String, Object> queryParameters;

    private final HttpServletRequest request;

    public RedirectUrlBuilder(HttpServletRequest request) {
        Assert.notNull(request, "Request argument cannot be null.");
        this.request = request;
    }

    public RedirectUrlBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Set whether to interpret a given URL that starts with a slash ("/") as relative to the current ServletContext,
     * i.e. as relative to the web application root.
     *
     * <p>Default is {@code false}: A URL that starts with a slash will be interpreted as absolute, i.e. taken as-is.
     * If {@code true}, the context path will be prepended to the URL in such a case.</p>
     *
     * @param contextRelative whether to interpret a given URL that starts with a slash ("/")
     *                        as relative to the current ServletContext, i.e. as relative to the
     *                        web application root.
     * @see javax.servlet.http.HttpServletRequest#getContextPath
     */
    public RedirectUrlBuilder setContextRelative(boolean contextRelative) {
        this.contextRelative = contextRelative;
        return this;
    }

    /**
     * Set the encoding scheme when encoding query parameters. Default is UTF-8.
     *
     * @param encodingScheme the encoding scheme when encoding query parameters. Default is UTF-8.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public RedirectUrlBuilder setEncodingScheme(String encodingScheme) {
        this.encodingScheme = encodingScheme;
        return this;
    }

    public RedirectUrlBuilder setQueryParameters(Map<String, Object> params) {
        this.queryParameters = params;
        return this;
    }

    public String build() {

        StringBuilder targetUrl = new StringBuilder();

        if (this.contextRelative && this.url.startsWith("/")) {
            // Do not apply context path to relative URLs.
            targetUrl.append(request.getContextPath());
        }
        targetUrl.append(this.url);

        try {
            appendQueryProperties(targetUrl);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Specified encodingScheme is not valid: " + e.getMessage(), e);
        }

        return targetUrl.toString();
    }

    /**
     * Append query properties to the redirect URL. Stringifies, URL-encodes and formats query parameters.
     *
     * @param targetUrl the StringBuffer to append the properties to
     * @throws java.io.UnsupportedEncodingException if string encoding failed
     */
    protected void appendQueryProperties(StringBuilder targetUrl)
        throws UnsupportedEncodingException {

        // Extract anchor fragment, if any.
        // The following code does not use JDK 1.4's StringBuffer.indexOf(String)
        // method to retain JDK 1.3 compatibility.
        String fragment = null;
        int anchorIndex = targetUrl.toString().indexOf('#');
        if (anchorIndex > -1) {
            fragment = targetUrl.substring(anchorIndex);
            targetUrl.delete(anchorIndex, targetUrl.length());
        }

        // If there aren't already some parameters, we need a "?".
        boolean first = (this.url.indexOf('?') < 0);

        if (queryParameters != null) {
            for (Object o : queryParameters.entrySet()) {
                if (first) {
                    targetUrl.append('?');
                    first = false;
                } else {
                    targetUrl.append('&');
                }
                Map.Entry entry = (Map.Entry) o;
                String encodedKey = urlEncode(entry.getKey().toString());
                String encodedValue =
                    (entry.getValue() != null ? urlEncode(entry.getValue().toString()) : "");
                targetUrl.append(encodedKey).append('=').append(encodedValue);
            }
        }

        // Append anchor fragment, if any, to end of URL.
        if (fragment != null) {
            targetUrl.append(fragment);
        }
    }

    /**
     * URL-encode the given input String with the given encoding scheme, using
     * {@link java.net.URLEncoder#encode(String, String) URLEncoder.encode(input, enc)}.
     *
     * @param input the unencoded input String
     * @return the encoded output String
     * @see java.net.URLEncoder#encode(String, String)
     * @see java.net.URLEncoder#encode(String)
     */
    protected String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, encodingScheme);
        } catch (UnsupportedEncodingException e) {
            String msg = "Unsupported character encoding '" + encodingScheme + "': " + e.getMessage();
            throw new IllegalArgumentException(msg, e);
        }
    }
}
