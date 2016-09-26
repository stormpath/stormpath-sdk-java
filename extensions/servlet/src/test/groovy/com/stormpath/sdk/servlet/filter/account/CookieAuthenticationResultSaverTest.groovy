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
package com.stormpath.sdk.servlet.filter.account

import com.stormpath.sdk.servlet.config.CookieConfig
import com.stormpath.sdk.servlet.http.Resolver
import com.stormpath.sdk.servlet.util.SecureRequiredExceptForLocalhostResolver
import org.powermock.modules.testng.PowerMockTestCase
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.expect
import static org.powermock.api.easymock.PowerMock.createMock
import static org.powermock.api.easymock.PowerMock.replay
import static org.powermock.api.easymock.PowerMock.verify
import static org.testng.Assert.assertFalse

/**
 * @since 1.0.RC9
 */
class CookieAuthenticationResultSaverTest extends PowerMockTestCase {

    /**
     * https://github.com/stormpath/stormpath-sdk-java/issues/937
     */
    @Test(expectedExceptions = InsecureCookieException.class)
    void testIsCookieSecureOverHttp() {
        HttpServletRequest request = createMock(HttpServletRequest.class)
        CookieConfig accessTokenConfig = createMock(CookieConfig.class)
        CookieConfig refreshTokenConfig = createMock(CookieConfig.class)

        //Let's make the IsRequestSecureResolver return true
        def resolver = new Resolver<Boolean>() {
            @Override
            Boolean get(HttpServletRequest r, HttpServletResponse response) {
                return true
            }
        }

        expect(accessTokenConfig.isSecure()).andReturn(true)
        expect(request.getScheme()).andReturn "http"

        replay request, accessTokenConfig, refreshTokenConfig

        def saver = new CookieAuthenticationResultSaver(accessTokenConfig, refreshTokenConfig, resolver)

        assertFalse saver.isCookieSecure(request, accessTokenConfig)

        verify request, refreshTokenConfig, refreshTokenConfig
    }

    /**
     * https://github.com/stormpath/stormpath-sdk-java/issues/409
     */
    @Test
    void testIsCookieSecureWithLocalhost() {

        HttpServletRequest request = createMock(HttpServletRequest.class)
        CookieConfig accessTokenConfig = createMock(CookieConfig.class)
        CookieConfig refreshTokenConfig = createMock(CookieConfig.class)
        def localhost = createMock(Resolver.class)

        def resolver = new SecureRequiredExceptForLocalhostResolver(localhost) {
            @Override
            Boolean get(HttpServletRequest req, HttpServletResponse resp) {
                return false;
            }
        }

        expect(accessTokenConfig.isSecure()).andReturn(true)
        expect(request.getScheme()).andReturn "https"

        replay request, accessTokenConfig, refreshTokenConfig, localhost

        def saver = new CookieAuthenticationResultSaver(accessTokenConfig, refreshTokenConfig, resolver)

        assertFalse saver.isCookieSecure(request, accessTokenConfig)

        verify request, refreshTokenConfig, refreshTokenConfig, localhost
    }
}
