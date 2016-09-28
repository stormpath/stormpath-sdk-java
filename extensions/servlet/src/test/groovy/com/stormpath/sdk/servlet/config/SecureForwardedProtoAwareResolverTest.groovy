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

import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertTrue

/**
 * https://github.com/stormpath/stormpath-sdk-java/issues/139
 *
 * @since 1.1.0
 */
class SecureForwardedProtoAwareResolverTest {

    @Test
    void testXForwardedProtoIsHttps() {
        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)

        expect(request.getHeader("X-Forwarded-Proto")).andReturn "https"

        replay request, response

        def isSecureResolver = new SecureForwardedProtoAwareResolver()

        assertTrue isSecureResolver.get(request, response)

        verify request, response
    }

    @Test
    void testXForwardedProtoIsHTTPS() {
        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)

        expect(request.getHeader("X-Forwarded-Proto")).andReturn "HTTPS"

        replay request, response

        def isSecureResolver = new SecureForwardedProtoAwareResolver()

        assertTrue isSecureResolver.get(request, response)

        verify request, response
    }

    @Test
    void testXForwardedProtoIsHttp() {
        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)

        expect(request.getHeader("X-Forwarded-Proto")).andReturn "http"

        replay request, response

        def isSecureResolver = new SecureForwardedProtoAwareResolver()

        assertFalse isSecureResolver.get(request, response)

        verify request, response
    }

    @Test
    void testXForwardedProtoIsNull() {
        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)

        expect(request.getHeader("X-Forwarded-Proto")).andReturn null

        replay request, response

        def isSecureResolver = new SecureForwardedProtoAwareResolver()

        assertFalse isSecureResolver.get(request, response)

        verify request, response
    }


}
