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
package com.stormpath.sdk.http;

/**
 * HttpMethod is a subset of the methods defined as part of the
 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">Hypertext Transfer Protocol -- HTTP/1.1</a>
 * specification.
 *
 * @since 1.0.RC
 */
public enum HttpMethod {

    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    /**
     * Retrieves the {@link HttpMethod} instance associated to the name.
     *
     * @param name - The name of the {@code Method} instance to retrieve.
     * @return - Method mapped to {@code name} argument.
     * @throws IllegalArgumentException - If name is null or if cannot be mapped to an actual method.
     */
    public static HttpMethod fromName(String name) {
        for (HttpMethod method : values()) {
            if (method.name().equalsIgnoreCase(name)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unrecognized method name: " + name);
    }
}
