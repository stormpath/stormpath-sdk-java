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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An http {@code Resolver} is able to resolve (locate) a value by inspecting a request.
 *
 * @param <T> the type of value returned by the resolver
 * @since 1.0.RC3
 */
public interface Resolver<T> {

    /**
     * Returns the discovered value or {@code null} if the value could not be discovered.
     *
     * @param request  the inbound request
     * @param response the outbound response
     * @return the discovered value or {@code null} if the value could not be discovered.
     */
    T get(HttpServletRequest request, HttpServletResponse response);
}
