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
package com.stormpath.sdk.servlet.config

import com.stormpath.sdk.servlet.http.Resolver
import com.stormpath.sdk.servlet.util.SecureRequiredExceptForLocalhostResolver

import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import org.testng.annotations.Test
import static org.testng.Assert.*

import javax.servlet.http.HttpServletRequest

/**
 * @since 1.1.0
 */
class IsRequestSecureResolverTest {

    @Test
    void testHttpsIsSecure() {
        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)
        Resolver<Boolean> secureRequiredExceptForLocalhostResolver = createMock(SecureRequiredExceptForLocalhostResolver.class)
        Resolver<Boolean> secureForwardedProtoAwareResolver = createMock(SecureForwardedProtoAwareResolver.class)


        expect(request.getScheme()).andReturn "https"

        replay request, response, secureRequiredExceptForLocalhostResolver, secureForwardedProtoAwareResolver

        def isSecureResolver = new IsRequestSecureResolver(secureRequiredExceptForLocalhostResolver, secureForwardedProtoAwareResolver)

        assertTrue isSecureResolver.get(request, response)

        verify request, response, secureRequiredExceptForLocalhostResolver, secureForwardedProtoAwareResolver
    }

    @Test
    void testLocalhostIsSecure() {
        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)
        Resolver<Boolean> secureRequiredExceptForLocalhostResolver = createMock(SecureRequiredExceptForLocalhostResolver.class)
        Resolver<Boolean> secureForwardedProtoAwareResolver = createMock(SecureForwardedProtoAwareResolver.class)

        expect(request.getScheme()).andReturn "http"
        expect(secureRequiredExceptForLocalhostResolver.get(request, response)).andReturn true

        replay request, response, secureRequiredExceptForLocalhostResolver, secureForwardedProtoAwareResolver

        def isSecureResolver = new IsRequestSecureResolver(secureRequiredExceptForLocalhostResolver, secureForwardedProtoAwareResolver)

        assertTrue isSecureResolver.get(request, response)

        verify request, response, secureRequiredExceptForLocalhostResolver, secureForwardedProtoAwareResolver
    }

    @Test
    void testXForwardedProtoIsSecure() {
        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)
        Resolver<Boolean> secureRequiredExceptForLocalhostResolver = createMock(SecureRequiredExceptForLocalhostResolver.class)
        Resolver<Boolean> secureForwardedProtoAwareResolver = createMock(SecureForwardedProtoAwareResolver.class)

        expect(request.getScheme()).andReturn "http"
        expect(secureRequiredExceptForLocalhostResolver.get(request, response)).andReturn false
        expect(secureForwardedProtoAwareResolver.get(request, response)).andReturn true

        replay request, response, secureRequiredExceptForLocalhostResolver, secureForwardedProtoAwareResolver

        def isSecureResolver = new IsRequestSecureResolver(secureRequiredExceptForLocalhostResolver, secureForwardedProtoAwareResolver)

        assertTrue isSecureResolver.get(request, response)

        verify request, response, secureRequiredExceptForLocalhostResolver, secureForwardedProtoAwareResolver
    }

    @Test
    void testXForwardedProtoNotPresent() {
        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)
        Resolver<Boolean> secureRequiredExceptForLocalhostResolver = createMock(SecureRequiredExceptForLocalhostResolver.class)
        Resolver<Boolean> secureForwardedProtoAwareResolver = createMock(SecureForwardedProtoAwareResolver.class)

        expect(request.getScheme()).andReturn "http"
        expect(secureRequiredExceptForLocalhostResolver.get(request, response)).andReturn false
        expect(secureForwardedProtoAwareResolver.get(request, response)).andReturn false

        replay request, response, secureRequiredExceptForLocalhostResolver, secureForwardedProtoAwareResolver

        def isSecureResolver = new IsRequestSecureResolver(secureRequiredExceptForLocalhostResolver, secureForwardedProtoAwareResolver)

        assertFalse isSecureResolver.get(request, response)

        verify request, response, secureRequiredExceptForLocalhostResolver, secureForwardedProtoAwareResolver
    }
}
