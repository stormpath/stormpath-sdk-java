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
package com.stormpath.sdk.servlet.filter.oauth.config;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.account.AuthenticationJwtFactory;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory;
import com.stormpath.sdk.servlet.filter.oauth.DefaultAccessTokenResultFactory;

import javax.servlet.ServletContext;

/**
 * @since 1.0.RC3
 */
public class AccessTokenResultFactoryFactory extends ConfigSingletonFactory<AccessTokenResultFactory> {

    protected static final String ACCOUNT_JWT_FACTORY = "stormpath.web.account.jwt.factory";

    @Override
    protected AccessTokenResultFactory createInstance(ServletContext servletContext) throws Exception {
        Application application = (Application)servletContext.getAttribute(Application.class.getName());
        AuthenticationJwtFactory factory = getConfig().getInstance(ACCOUNT_JWT_FACTORY);
        int ttl = getConfig().getAccountJwtTtl();
        return new DefaultAccessTokenResultFactory(application, factory, ttl);
    }
}
