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
package com.stormpath.sdk.impl.http;

/**
 * A canonical representation of a <a href="http://tools.ietf.org/html/rfc3986">Uniform Resource Identifier (URI)</a>
 * suitable for the SDK's needs.
 *
 * @since 1.0.RC4.3
 */
public interface CanonicalUri {

    /**
     * Returns a fully-qualified URI <em>without</em> {@code query} or {@code fragment} components.
     *
     * @return a fully-qualified URI <em>without</em> {@code query} or {@code fragment} components.
     */
    String getAbsolutePath();

    /**
     * Returns {@code true} if the URI has a {@code query} component, {@code false} otherwise.
     *
     * @return {@code true} if the URI has a {@code query} component, {@code false} otherwise.
     */
    boolean hasQuery();

    /**
     * Returns the URI's canonical {@code query} representation or {@code null} if there is no query component.
     *
     * @return the URI's canonical {@code query} representation or {@code null} if there is no query component.
     */
    QueryString getQuery();

}
