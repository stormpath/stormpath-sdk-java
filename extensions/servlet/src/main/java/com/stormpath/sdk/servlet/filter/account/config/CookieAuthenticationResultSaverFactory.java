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

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.filter.account.AccountCookieSecureEvaluator;
import com.stormpath.sdk.servlet.filter.account.AccountJwtFactory;
import com.stormpath.sdk.servlet.filter.account.CookieAuthenticationResultSaver;
import com.stormpath.sdk.servlet.http.Saver;

import javax.servlet.ServletContext;

public class CookieAuthenticationResultSaverFactory extends ConfigSingletonFactory<Saver<AuthenticationResult>> {

    protected static final String ACCOUNT_COOKIE_SECURE_EVALUATOR = "stormpath.web.account.cookie.secure.evaluator";
    protected static final String ACCOUNT_JWT_FACTORY = "stormpath.web.account.jwt.factory";

    @Override
    protected Saver<AuthenticationResult> createInstance(ServletContext servletContext) throws Exception {
        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);
        CookieConfig cookieConfig = config.getAccountCookieConfig();
        AccountCookieSecureEvaluator evaluator = config.getInstance(ACCOUNT_COOKIE_SECURE_EVALUATOR);
        AccountJwtFactory factory = config.getInstance(ACCOUNT_JWT_FACTORY);
        return new CookieAuthenticationResultSaver(cookieConfig, evaluator, factory);
    }
}


