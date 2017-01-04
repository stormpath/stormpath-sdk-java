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
package com.stormpath.sdk.servlet.config;

import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.util.SecureRequiredExceptForLocalhostResolver;

import javax.servlet.ServletContext;

/**
 * @since 1.0.RC3
 */
public class SecureResolverFactory extends ConfigSingletonFactory<Resolver<Boolean>> {

    public static final String LOCALHOST_RESOLVER = "stormpath.web.localhost.resolver";
    public static final String X_FORWARDED_PROTO_RESOLVER = "stormpath.web.xforwardedproto.resolver";

    @Override
    protected Resolver<Boolean> createInstance(ServletContext servletContext) throws Exception {
        Resolver<Boolean> localhostResolver = getConfig().getInstance(LOCALHOST_RESOLVER);
        Resolver<Boolean> xForwardedProtoResolver = getConfig().getInstance(X_FORWARDED_PROTO_RESOLVER);
        Resolver<Boolean> secureRequiredExceptForLocalhostResolver = new SecureRequiredExceptForLocalhostResolver(localhostResolver);
        return new IsRequestSecureResolver(secureRequiredExceptForLocalhostResolver, xForwardedProtoResolver);
    }
}
