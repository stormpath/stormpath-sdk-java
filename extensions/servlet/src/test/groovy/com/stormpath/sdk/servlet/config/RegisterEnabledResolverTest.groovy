package com.stormpath.sdk.servlet.config

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.servlet.application.ApplicationResolver
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @see <a href="https://github.com/stormpath/stormpath-sdk-java/issues/333">Issue 333</a>
 * @since 1.0.0
 */
class RegisterEnabledResolverTest {

    @Test
    public void testIsRegisterEnabledWhenPropertyIsFalse() {

        HttpServletRequest request = createMock(HttpServletRequest)
        HttpServletResponse response = createMock(HttpServletResponse)
        ApplicationResolver appResolver = createMock(ApplicationResolver)

        RegisterEnabledResolver resolver = new RegisterEnabledResolver(false);

        replay request, response, appResolver

        assertFalse resolver.get(request, response)

        verify request, response, appResolver
    }

    @Test
    public void testIsRegisterEnabledWhenPropertyIsTrueAndNoAppDefaultAccountStore() {

        HttpServletRequest request = createMock(HttpServletRequest)
        HttpServletResponse response = createMock(HttpServletResponse)
        ApplicationResolver appResolver = createMock(ApplicationResolver)
        Application application = createMock(Application)

        expect(appResolver.getApplication((HttpServletRequest)same(request))).andReturn(application)
        expect(application.getDefaultAccountStore()).andReturn(null)

        replay request, response, appResolver, application

        RegisterEnabledResolver resolver = new RegisterEnabledResolver(true, appResolver);

        assertFalse resolver.get(request, response)

        verify request, response, appResolver, application
    }

    @Test
    public void testIsRegisterEnabledWhenPropertyIsTrueAndAppDefaultAccountStore() {

        HttpServletRequest request = createMock(HttpServletRequest)
        HttpServletResponse response = createMock(HttpServletResponse)
        ApplicationResolver appResolver = createMock(ApplicationResolver)
        Application application = createMock(Application)
        AccountStore accountStore = createMock(AccountStore)

        expect(appResolver.getApplication((HttpServletRequest)same(request))).andReturn(application)
        expect(application.getDefaultAccountStore()).andReturn(accountStore)

        replay request, response, appResolver, application, accountStore

        RegisterEnabledResolver resolver = new RegisterEnabledResolver(true, appResolver);

        assertTrue resolver.get(request, response)
        assertFalse resolver.predicate.warned

        verify request, response, appResolver, application, accountStore
    }
}
