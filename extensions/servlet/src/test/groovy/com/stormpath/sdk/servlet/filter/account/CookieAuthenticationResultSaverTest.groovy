package com.stormpath.sdk.servlet.filter.account

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.Client
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

        replay request, accessTokenConfig, refreshTokenConfig, localhost

        def saver = new CookieAuthenticationResultSaver(accessTokenConfig, refreshTokenConfig, resolver)

        assertFalse saver.isCookieSecure(request, accessTokenConfig)

        verify request, refreshTokenConfig, refreshTokenConfig, localhost
    }
}
