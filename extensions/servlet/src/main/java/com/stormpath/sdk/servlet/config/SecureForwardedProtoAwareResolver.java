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

import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This {@link Resolver} inspects the request looking for the <code>X-Forwarded-Proto</code> header.
 * <p>If the value of the header equals <code>HTTPS</code> then it will return <code>true</code>. Otherwise it will return <code>false</code></p>
 * <p>This solves https://github.com/stormpath/stormpath-sdk-java/issues/139: support X-Forwarded-Proto HTTP header
 * if SSL termination is offloaded to dedicated hardware.</p>
 *
 * @since 1.1.0
 */
public class SecureForwardedProtoAwareResolver implements Resolver<Boolean> {

    private static final String HEADER_FORWARDED_PROTO = "X-Forwarded-Proto";
    private static final String HTTPS = "https";

    @Override
    public Boolean get(HttpServletRequest request, HttpServletResponse response) {
        String xForwardedProtoValue = request.getHeader(HEADER_FORWARDED_PROTO);
        return HTTPS.equalsIgnoreCase(xForwardedProtoValue);
    }
}
