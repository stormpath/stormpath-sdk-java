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

/**
 * Extraordinarily simple representation of an HTTP User agent that supplies only the features needed by the
 * default filter implementations.
 */
public interface UserAgent {

    /**
     * Returns true if the user agent is a REST client, false otherwise.  A REST client in this context is defined as
     * an HTTP User Agent that prefers JSON content over HTML content.  This is determined by looking at the
     * request {@code Accept} header and seeing if {@code application/json} appears before
     * {@code application/html}.
     *
     * @return true if the user agent is a REST client, false otherwise.
     */
    boolean isRestClient();
}
