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
