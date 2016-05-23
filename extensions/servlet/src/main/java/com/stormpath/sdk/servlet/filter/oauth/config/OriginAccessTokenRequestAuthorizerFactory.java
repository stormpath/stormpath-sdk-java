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

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.filter.oauth.OriginAccessTokenRequestAuthorizer;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @since 1.0.RC3
 */
public class OriginAccessTokenRequestAuthorizerFactory extends ConfigSingletonFactory<RequestAuthorizer> {

    public static final String LOCALHOST_RESOLVER = "stormpath.web.localhost.resolver";
    public static final String SERVER_URI_RESOLVER = "stormpath.web.oauth2.origin.authorizer.serverUriResolver";
    public static final String ORIGIN_URIS = "stormpath.web.oauth2.origin.authorizer.originUris";

    @Override
    protected RequestAuthorizer createInstance(ServletContext servletContext) throws Exception {

        ServerUriResolver resolver = getConfig().getInstance(SERVER_URI_RESOLVER);
        Resolver<Boolean> localhost = getConfig().getInstance(LOCALHOST_RESOLVER);
        String uris = getConfig().get(ORIGIN_URIS);

        Collection<String> additionalOriginUris = Collections.emptyList();

        if (Strings.hasText(uris)) {
            String[] values = Strings.split(uris);
            additionalOriginUris = Arrays.asList(values);
        }

        return new OriginAccessTokenRequestAuthorizer(resolver, localhost, additionalOriginUris);
    }
}
