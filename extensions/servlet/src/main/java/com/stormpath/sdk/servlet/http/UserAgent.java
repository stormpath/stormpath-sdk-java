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
package com.stormpath.sdk.servlet.http;

/**
 * Extraordinarily simple representation of an HTTP User agent that supplies only the features needed by the
 * default filter implementations.
 */
public interface UserAgent {

    /**
     * Returns {@code true} if the user agent is likely a web browser and not a library or command line client.  This
     * is a best effort evaluation - it does not protect against user agents that spoof the {@code User-Agent} header
     * of known browsers.
     *
     * @return {@code true} if the user agent is likely a web browser and not a library or command line client.
     */
    boolean isBrowser();

    /**
     * Returns {@code true} if the user agent prefers HTML (or XHTML) content, {@code false} otherwise.  HTML preference
     * in this context is determined by looking at the {@code Accept} header and seeing if
     * {@code application/html} or {@code application/xhtml+xml} are listed first before
     * {@code application/json}.
     *
     * @return {@code true} if the user agent prefers HTML (or XHTML) content, {@code false} otherwise.
     */
    boolean isHtmlPreferred();
}
