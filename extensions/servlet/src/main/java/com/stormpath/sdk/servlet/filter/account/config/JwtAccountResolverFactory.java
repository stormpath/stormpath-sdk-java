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

import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.account.DefaultJwtAccountResolver;
import com.stormpath.sdk.servlet.filter.account.JwtAccountResolver;
import com.stormpath.sdk.servlet.filter.account.JwtSigningKeyResolver;
import com.stormpath.sdk.servlet.oauth.impl.JwtTokenSigningKeyResolver;

import javax.servlet.ServletContext;

/**
 * @since 1.0.RC3
 */
public class JwtAccountResolverFactory extends ConfigSingletonFactory<JwtAccountResolver> {

    @Override
    protected JwtAccountResolver createInstance(ServletContext servletContext) throws Exception {
        JwtSigningKeyResolver resolver = new JwtTokenSigningKeyResolver();
        return new DefaultJwtAccountResolver(resolver);
    }
}
