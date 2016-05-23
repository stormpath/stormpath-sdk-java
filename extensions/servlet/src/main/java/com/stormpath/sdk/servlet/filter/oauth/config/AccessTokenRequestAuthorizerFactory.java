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

import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.oauth.DefaultAccessTokenRequestAuthorizer;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.ServletContext;

/**
 * @since 1.0.RC3
 */
public class AccessTokenRequestAuthorizerFactory extends ConfigSingletonFactory<RequestAuthorizer> {

    public static final String SECURE_RESOLVER = "stormpath.web.oauth2.authorizer.secure.resolver";
    public static final String ORIGIN_AUTHORIZER = "stormpath.web.oauth2.origin.authorizer";

    @Override
    protected RequestAuthorizer createInstance(ServletContext servletContext) throws Exception {
        Resolver<Boolean> condition = getConfig().getInstance(SECURE_RESOLVER);
        RequestAuthorizer originAuthorizer = getConfig().getInstance(ORIGIN_AUTHORIZER);
        return new DefaultAccessTokenRequestAuthorizer(condition, originAuthorizer);
    }
}
