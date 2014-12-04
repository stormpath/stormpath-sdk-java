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
package com.stormpath.sdk.servlet.filter.oauth.config;

import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenRequestAuthorizer;
import com.stormpath.sdk.servlet.filter.oauth.OriginAccessTokenRequestAuthorizer;
import com.stormpath.sdk.servlet.util.RequestCondition;

import javax.servlet.ServletContext;

public class AccessTokenRequestAuthorizerFactory extends ConfigSingletonFactory<AccessTokenRequestAuthorizer> {

    public static final String SERVER_URI_RESOLVER = "stormpath.web.accessToken.authorizer.serverUriResolver";
    public static final String SECURE_CONDITION = "stormpath.web.accessToken.authorizer.secure.condition";

    @Override
    protected AccessTokenRequestAuthorizer createInstance(ServletContext servletContext) throws Exception {
        ServerUriResolver resolver = getConfig().getInstance(SERVER_URI_RESOLVER);
        RequestCondition condition = getConfig().getInstance(SECURE_CONDITION);
        return new OriginAccessTokenRequestAuthorizer(resolver, condition);
    }
}
