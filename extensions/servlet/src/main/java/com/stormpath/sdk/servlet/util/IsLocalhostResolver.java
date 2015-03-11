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
package com.stormpath.sdk.servlet.util;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC3
 */
public class IsLocalhostResolver implements Resolver<Boolean> {

    private final Resolver<String> remoteAddrResolver;

    @SuppressWarnings("UnusedDeclaration") //used via reflection by servlet config (stormpath.web.localhost.resolver)
    public IsLocalhostResolver() {
        this(new RemoteAddrResolver());
    }

    public IsLocalhostResolver(Resolver<String> remoteAddrResolver) {
        Assert.notNull(remoteAddrResolver, "remoteAddrResolver cannot be null.");
        this.remoteAddrResolver = remoteAddrResolver;
    }

    @Override
    public Boolean get(HttpServletRequest request, HttpServletResponse response) {

        String host = getRemoteAddr(request, response);

        return host != null && (host.equalsIgnoreCase("localhost") ||
                                host.equals("127.0.0.1") ||
                                host.startsWith("::1") ||
                                host.startsWith("0:0:0:0:0:0:0:1"));
    }

    protected String getRemoteAddr(HttpServletRequest request, HttpServletResponse response) {
        return this.remoteAddrResolver.get(request, response);
    }
}
