/*
 * Copyright 2016 Stormpath, Inc.
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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.util.SecureRequiredExceptForLocalhostResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Resolver that will determine if the request is secure or not.
 *
 * <p>It will decide so based on either criteria: 1) if the current request is <code>HTTPS</code> or if 2) the request
 * contains an <code>X-Forwarded-Proto</code> header whose value equals <code>HTTPS</code>.</p>
 * <p>This solves https://github.com/stormpath/stormpath-sdk-java/issues/139: support X-Forwarded-Proto HTTP header
 * if SSL termination is offloaded to dedicated hardware.</p>
 *
 * @see SecureRequiredExceptForLocalhostResolver
 * @see SecureForwardedProtoAwareResolver
 *
 * @since 1.1.0
 */
public class IsRequestSecureResolver implements Resolver<Boolean> {

    private final Resolver<Boolean> secureRequiredExceptForLocalhostResolver;
    private final Resolver<Boolean> secureForwardedProtoAwareResolver;
    private static final String HTTPS = "https";

    public IsRequestSecureResolver(Resolver<Boolean> secureRequiredExceptForLocalhostResolver, Resolver<Boolean> secureForwardedProtoAwareResolver) {
        Assert.notNull(secureRequiredExceptForLocalhostResolver, "secureRequiredExceptForLocalhostResolver resolver cannot be null.");
        Assert.notNull(secureForwardedProtoAwareResolver, "secureForwardedProtoAwareResolver resolver cannot be null.");
        this.secureRequiredExceptForLocalhostResolver = secureRequiredExceptForLocalhostResolver;
        this.secureForwardedProtoAwareResolver = secureForwardedProtoAwareResolver;
    }

    @Override
    public Boolean get(HttpServletRequest request, HttpServletResponse response) {
        if (HTTPS.equals(request.getScheme())) {
            return true; //the request is HTTPS
        }

        boolean result = secureRequiredExceptForLocalhostResolver.get(request, response); //is the request coming from localhost?
        if (!result) {
            //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/139
            result = secureForwardedProtoAwareResolver.get(request, response); //does the request have a X-Forwarded-Proto header whose value is HTTPS?
        }
        return result;
    }
}
