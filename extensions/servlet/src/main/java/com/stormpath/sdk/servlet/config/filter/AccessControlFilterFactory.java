/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.AccessControlFilter;
import com.stormpath.sdk.servlet.filter.UnauthenticatedHandler;
import com.stormpath.sdk.servlet.filter.UnauthorizedHandler;

import javax.servlet.ServletContext;

/**
 * @since 1.0.0
 * @param <T>
 */
public abstract class AccessControlFilterFactory<T extends AccessControlFilter> extends FilterFactory<T> {

    protected static final String UNAUTHENTICATED_HANDLER = "stormpath.web.authc.unauthenticatedHandler";
    protected static final String UNAUTHORIZED_HANDLER = "stormpath.web.authz.unauthorizedHandler";

    @Override
    protected T createInstance(ServletContext servletContext, Config config) throws Exception {

        T f = newInstance();

        f.setLoginUrl(config.getLoginConfig().getUri());
        f.setAccessTokenUrl(config.getAccessTokenUrl());

        UnauthenticatedHandler authc = config.getInstance(UNAUTHENTICATED_HANDLER);
        f.setUnauthenticatedHandler(authc);

        UnauthorizedHandler authz = config.getInstance(UNAUTHORIZED_HANDLER);
        f.setUnauthorizedHandler(authz);

        configure(f, config);

        return f;
    }

    protected abstract void configure(T f, Config config);

    protected abstract T newInstance();
}
