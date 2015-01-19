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
package com.stormpath.sdk.servlet.filter.account.config;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.filter.account.CookieAccountResolver;
import com.stormpath.sdk.servlet.filter.account.JwtAccountResolver;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.ServletContext;

/**
 * @since 1.0.RC3
 */
public class CookieAccountResolverFactory extends ConfigSingletonFactory<Resolver<Account>> {

    protected static final String JWT_ACCOUNT_RESOLVER = "stormpath.web.account.jwt.resolver";

    @Override
    protected Resolver<Account> createInstance(ServletContext servletContext) throws Exception {
        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);
        CookieConfig cookieConfig = config.getAccountCookieConfig();
        JwtAccountResolver resolver = config.getInstance(JWT_ACCOUNT_RESOLVER);
        return new CookieAccountResolver(cookieConfig, resolver);
    }
}
