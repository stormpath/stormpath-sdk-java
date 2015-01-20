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
 * Requires all HTTP clients to be secure (that is, {@link javax.servlet.http.HttpServletRequest#isSecure()
 * request.isSecure()} must be {@code true}) <em>except</em> for localhost clients.  If the HTTP client is a localhost
 * client, the request does not need to be secure.
 *
 * <p>This allows for the common use case where behavior typically always needs to be secure over a TLS connection (e.g.
 * a login attempt) except during development, typically done on localhost.</p>
 *
 * @since 1.0.RC3
 */
public class SecureRequiredExceptForLocalhostResolver implements Resolver<Boolean> {

    private final Resolver<Boolean> localhostResolver;

    public SecureRequiredExceptForLocalhostResolver(Resolver<Boolean> localhostResolver) {
        Assert.notNull(localhostResolver, "localhost resolver cannot be null.");
        this.localhostResolver = localhostResolver;
    }

    /**
     * Returns {@code true} for all clients <em>except</em> localhost clients, {@code false} if the client is a
     * localhost client.
     *
     * @param request  inbound request
     * @param response outbound response
     * @return {@code true} for all clients <em>except</em> localhost clients, {@code false} if the client is a
     * localhost client.
     */
    @Override
    public Boolean get(HttpServletRequest request, HttpServletResponse response) {
        //secure must be request except if the client is coming from localhost:
        boolean isLocalhost = this.localhostResolver.get(request, response);
        return !isLocalhost;
    }
}
