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

import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.oauth.DefaultAccessTokenRequestAuthorizer;
import com.stormpath.sdk.servlet.util.RequestCondition;

import javax.servlet.ServletContext;

public class AccessTokenRequestAuthorizerFactory extends ConfigSingletonFactory<RequestAuthorizer> {

    public static final String SECURE_CONDITION = "stormpath.web.accessToken.authorizer.secure.condition";
    public static final String ORIGIN_AUTHORIZER = "stormpath.web.accessToken.origin.authorizer";

    @Override
    protected RequestAuthorizer createInstance(ServletContext servletContext) throws Exception {
        RequestCondition condition = getConfig().getInstance(SECURE_CONDITION);
        RequestAuthorizer originAuthorizer = getConfig().getInstance(ORIGIN_AUTHORIZER);
        return new DefaultAccessTokenRequestAuthorizer(condition, originAuthorizer);
    }
}
