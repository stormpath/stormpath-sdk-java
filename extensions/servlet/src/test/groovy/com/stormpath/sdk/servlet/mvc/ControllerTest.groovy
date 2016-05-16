package com.stormpath.sdk.servlet.mvc

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.servlet.account.DefaultAccountResolver
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 *
 */
class ControllerTest {
    @Test
    void testDoPostIfAllowIfAuthenticated() {
        ViewModel expectedViewModel = new DefaultViewModel()

        Controller controller = new AbstractController() {
            @Override
            protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
                return expectedViewModel
            }

            @Override
            boolean isNotAllowedIfAuthenticated() {
                return false
            }
        }

        controller.nextUri = "test"

        HttpServletRequest request = createStrictMock(HttpServletRequest)
        HttpServletResponse response = createStrictMock(HttpServletResponse)

        expect(request.getMethod()).andReturn "POST"
        expect(request.getAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME)).andReturn createStrictMock(Account)

        replay request, response

        ViewModel viewModel = controller.handleRequest(request, response)

        assertEquals viewModel, expectedViewModel
        assertFalse viewModel.redirect

        verify request, response
    }

    @Test
    void testReturn403OnPostIfNotAllowIfAuthenticated() {
        Controller controller = new AbstractController() {
            @Override
            boolean isNotAllowedIfAuthenticated() {
                return true
            }
        }

        controller.nextUri = "test"

        HttpServletRequest request = createStrictMock(HttpServletRequest)
        HttpServletResponse response = createStrictMock(HttpServletResponse)

        expect(request.getMethod()).andReturn "POST"
        expect(request.getAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME)).andReturn createStrictMock(Account)
        expect(response.sendError(403))
        expect(response.getStatus()).andReturn 403

        replay request, response

        ViewModel viewModel = controller.handleRequest(request, response)

        assertNull viewModel
        assertEquals response.getStatus(), 403

        verify request, response
    }

    @Test
    void testCallDoGetIfAllowIfAuthenticated() {
        ViewModel expectedViewModel = new DefaultViewModel()

        Controller controller = new AbstractController() {
            @Override
            protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
                return expectedViewModel
            }

            @Override
            boolean isNotAllowedIfAuthenticated() {
                return true
            }
        }

        controller.nextUri = "test"

        HttpServletRequest request = createStrictMock(HttpServletRequest)
        HttpServletResponse response = createStrictMock(HttpServletResponse)

        expect(request.getMethod()).andReturn "GET"
        expect(request.getAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME)).andReturn null
        expect(request.getSession(false)).andReturn null

        replay request, response

        ViewModel viewModel = controller.handleRequest(request, response)

        assertEquals viewModel, expectedViewModel
        assertFalse viewModel.redirect

        verify request, response
    }

    @Test
    void testRedirectGetRequestIfNotAllowIfAuthenticated() {
        Controller controller = new AbstractController() {
            @Override
            boolean isNotAllowedIfAuthenticated() {
                return true
            }
        }

        controller.nextUri = "test"

        HttpServletRequest request = createStrictMock(HttpServletRequest)
        HttpServletResponse response = createStrictMock(HttpServletResponse)

        expect(request.getMethod()).andReturn "GET"
        expect(request.getAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME)).andReturn createStrictMock(Account)

        replay request, response

        ViewModel viewModel = controller.handleRequest(request, response)

        assertEquals viewModel.viewName, controller.nextUri
        assertTrue viewModel.redirect

        verify request, response
    }

    @Test(enabled = false)
    void testControllersThatShouldAllowIfAuthenticated() {
        [
                new LogoutController(),
                new SamlLogoutController(),
                new IdSiteLogoutController(),
                new ChangePasswordController()
        ].each {
            assertFalse it.isNotAllowedIfAuthenticated()
        }
    }

    @Test(enabled = false)
    void testControllersThatShouldNotAllowIfAuthenticated() {
        [
                new AccessTokenController(),
                new ForgotPasswordController(),
                new IdSiteController(),
                new IdSiteResultController(),
                new LoginController(),
                new RegisterController(),
                new SamlController(),
                new SamlResultController(),
                new SendVerificationEmailController(),
                new VerifyController()
        ].each {
            assertTrue it.isNotAllowedIfAuthenticated()
        }
    }
}
