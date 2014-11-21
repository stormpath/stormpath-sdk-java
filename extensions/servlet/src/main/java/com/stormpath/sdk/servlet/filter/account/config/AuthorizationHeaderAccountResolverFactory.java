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
package com.stormpath.sdk.servlet.filter.account.config;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.Factory;
import com.stormpath.sdk.servlet.filter.account.AuthorizationHeaderAccountResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticator;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class AuthorizationHeaderAccountResolverFactory implements ServletContextInitializable, Factory<Resolver<Account>> {

    public static final String HTTP_AUTHENTICATOR = "stormpath.servlet.http.authc";

    private Resolver<Account> instance;

    @Override
    public void init(ServletContext servletContext) throws ServletException {
        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);
        HttpAuthenticator httpAuthenticator = config.getInstance(HTTP_AUTHENTICATOR);
        this.instance = new AuthorizationHeaderAccountResolver(httpAuthenticator);
    }

    @Override
    public Resolver<Account> getInstance() {
        return this.instance;
    }
}
