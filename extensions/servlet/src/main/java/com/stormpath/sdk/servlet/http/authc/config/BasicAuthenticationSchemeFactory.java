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
package com.stormpath.sdk.servlet.http.authc.config;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.http.authc.AuthenticationAccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.BasicAuthenticationScheme;

import javax.servlet.ServletContext;

public class BasicAuthenticationSchemeFactory extends ConfigSingletonFactory<BasicAuthenticationScheme> {

    @Override
    protected BasicAuthenticationScheme createInstance(ServletContext servletContext) throws Exception {
        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);
        AuthenticationAccountStoreResolver resolver = config.getInstance("stormpath.servlet.http.authc.accountStoreResolver");
        return new BasicAuthenticationScheme(resolver);
    }
}
