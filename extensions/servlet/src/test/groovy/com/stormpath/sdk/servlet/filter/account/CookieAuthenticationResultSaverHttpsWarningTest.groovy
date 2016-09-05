package com.stormpath.sdk.servlet.filter.account

import com.stormpath.sdk.servlet.config.CookieConfig
import com.stormpath.sdk.servlet.http.Resolver
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.testng.PowerMockTestCase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.isA
import static org.easymock.EasyMock.isNull
import static org.easymock.EasyMock.same
import static org.powermock.api.easymock.PowerMock.createMock
import static org.powermock.api.easymock.PowerMock.mockStatic
import static org.powermock.api.easymock.PowerMock.replay
import static org.powermock.api.easymock.PowerMock.reset
import static org.powermock.api.easymock.PowerMock.verify
import static org.testng.Assert.assertFalse

/**
 * @since 1.0.RC9
 */
@PrepareForTest(LoggerFactory.class)
class CookieAuthenticationResultSaverHttpsWarningTest extends PowerMockTestCase {

    /**
     * https://github.com/stormpath/stormpath-sdk-java/issues/409
     */
    @Test
    void testIsCookieSecureWithConfigFalse() {

        mockStatic(LoggerFactory.class)
        Logger log = createMock(Logger.class)
        expect(LoggerFactory.getLogger((Class) isA(Class))).andReturn(log)

        HttpServletRequest request = createMock(HttpServletRequest.class)
        CookieConfig accessTokenConfig = createMock(CookieConfig.class)
        CookieConfig refreshTokenConfig = createMock(CookieConfig.class)

        def resolver = createMock(Resolver.class)

        expect(accessTokenConfig.isSecure()).andReturn(true)
        expect(resolver.get(same(request), isNull(HttpServletResponse))).andReturn(false)
        expect(request.getScheme()).andReturn "https"
        expect(log.warn(isA(String)))

        replay LoggerFactory.class
        replay log, request, accessTokenConfig, refreshTokenConfig, resolver

        def saver = new CookieAuthenticationResultSaver(accessTokenConfig, refreshTokenConfig, resolver)
        assertFalse saver.isCookieSecure(request, accessTokenConfig)

        verify LoggerFactory.class
        verify log, request, accessTokenConfig, refreshTokenConfig, resolver
        reset LoggerFactory.class
    }
}
