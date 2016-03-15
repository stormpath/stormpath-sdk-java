package com.stormpath.sdk.servlet.filter.account

import com.stormpath.sdk.servlet.config.CookieConfig
import com.stormpath.sdk.servlet.http.Resolver
import com.stormpath.sdk.servlet.util.SecureRequiredExceptForLocalhostResolver
import org.powermock.modules.testng.PowerMockTestCase
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.expect
import static org.powermock.api.easymock.PowerMock.*
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
        CookieConfig config = createMock(CookieConfig.class)
        AuthenticationJwtFactory factory = createMock(AuthenticationJwtFactory.class)
        def localhost = createMock(Resolver.class)

        def resolver = new SecureRequiredExceptForLocalhostResolver(localhost) {
            @Override
            Boolean get(HttpServletRequest req, HttpServletResponse resp) {
                return false;
            }
        }

        expect(config.isSecure()).andReturn(true)

        replay request, config, localhost, factory

        def saver = new CookieAuthenticationResultSaver(config, resolver, factory)

        assertFalse saver.isCookieSecure(request, config)

        verify request, config, localhost, factory
    }
}
