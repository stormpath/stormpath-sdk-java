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
package com.stormpath.sdk.servlet;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.application.DefaultApplicationResolver;
import com.stormpath.sdk.servlet.client.DefaultClientResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

/** @since 1.0 */
public final class Servlets {

    public static Client getClient(ServletContext servletContext) {
        return new DefaultClientResolver().getClient(servletContext);
    }

    public static Client getClient(ServletRequest request) {
        return getClient(request.getServletContext());
    }

    public static Application getApplication(ServletContext servletContext) {
        return new DefaultApplicationResolver().getApplication(servletContext);
    }

    public static Application getApplication(ServletRequest servletRequest) {
        return getApplication(servletRequest.getServletContext());
    }
}
