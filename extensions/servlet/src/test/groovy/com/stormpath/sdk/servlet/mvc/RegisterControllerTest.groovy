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
package com.stormpath.sdk.servlet.mvc

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.impl.account.DefaultAccount
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager
import com.stormpath.sdk.servlet.event.RequestEvent
import com.stormpath.sdk.servlet.event.impl.Publisher
import com.stormpath.sdk.servlet.form.DefaultForm
import com.stormpath.sdk.servlet.form.Field
import com.stormpath.sdk.servlet.form.Form
import com.stormpath.sdk.servlet.http.MediaType
import com.stormpath.sdk.servlet.http.UserAgents
import com.stormpath.sdk.servlet.http.impl.DefaultUserAgent
import org.testng.Assert
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull

/**
 * @since 1.0.0
 */
public class RegisterControllerTest {

    @Test
    void testPreRegisterHandlerAndContinueNormalWorkflow() {
        WebHandler registerPreHandler = createMock(WebHandler)
        HttpServletRequest request = createMock(HttpServletRequest)
        HttpServletResponse response = createMock(HttpServletResponse)
        Form form = DefaultForm.builder().setFields(new ArrayList<Field>()).build()
        Client client = createMock(Client)
        Account account = createNiceMock(Account)
        CsrfTokenManager csrfTokenManager = createMock(CsrfTokenManager)
        RequestFieldValueResolver requestFieldValueResolver = createMock(RequestFieldValueResolver)
        Application application = createMock(Application)
        CustomData customData = createMock(CustomData)
        Publisher<RequestEvent> eventPublisher = createMock(Publisher)

        RegisterController registerController = new RegisterController(
                client: client,
                preRegisterHandler: registerPreHandler,
                csrfTokenManager: csrfTokenManager,
                fieldValueResolver: requestFieldValueResolver,
                produces: Arrays.asList(MediaType.TEXT_HTML),
                eventPublisher: eventPublisher
        )

        expect(client.instantiate(Account.class)).andReturn account
        expect(requestFieldValueResolver.getAllFields(request)).andReturn new HashMap<String, Object>()
        expect(request.getAttribute(Application.class.getName())).andReturn ((Application)application)
        expect(registerPreHandler.handle(request, response, account)).andReturn true
        expect(account.setGivenName("UNKNOWN")).andReturn account
        expect(account.setSurname("UNKNOWN")).andReturn account
        expect(account.getCustomData()).andReturn customData
        expect(application.createAccount(account)).andReturn account
        expect(eventPublisher.publish(anyObject()))

        expect(request.getAttribute(UserAgents.USER_AGENT_REQUEST_ATTRIBUTE_NAME)).andReturn new DefaultUserAgent(request)
        expect(request.getHeader("Accept")).andReturn "text/html"

        replay eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account

        def vm = registerController.onValidSubmit(request, response, form)

        verify eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account

        assertNotNull(vm, "ViewModel should not be empty")
    }

    @Test
    void testPreRegisterHandlerAndAbortNormalWorkflow() {
        WebHandler registerPreHandler = createMock(WebHandler)
        HttpServletRequest request = createMock(HttpServletRequest)
        HttpServletResponse response = createMock(HttpServletResponse)
        Form form = DefaultForm.builder().setFields(new ArrayList<Field>()).build()
        Client client = createMock(Client)
        Account account = createNiceMock(Account)
        CsrfTokenManager csrfTokenManager = createMock(CsrfTokenManager)
        RequestFieldValueResolver requestFieldValueResolver = createMock(RequestFieldValueResolver)
        Application application = createMock(Application)
        CustomData customData = createMock(CustomData)
        Publisher<RequestEvent> eventPublisher = createMock(Publisher)

        RegisterController registerController = new RegisterController(
                client: client,
                preRegisterHandler: registerPreHandler,
                csrfTokenManager: csrfTokenManager,
                fieldValueResolver: requestFieldValueResolver,
                produces: Arrays.asList(MediaType.TEXT_HTML),
                eventPublisher: eventPublisher
        )

        expect(client.instantiate(Account.class)).andReturn account
        expect(requestFieldValueResolver.getAllFields(request)).andReturn new HashMap<String, Object>()
        expect(request.getAttribute(Application.class.getName())).andReturn ((Application)application)
        expect(registerPreHandler.handle(request, response, account)).andReturn false
        expect(account.setGivenName("UNKNOWN")).andReturn account
        expect(account.setSurname("UNKNOWN")).andReturn account
        expect(account.getCustomData()).andReturn customData

        replay eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account

        def vm = registerController.onValidSubmit(request, response, form)

        verify eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account

        assertNull(vm, "ViewModel should be empty")
    }

    @Test
    void testPostRegisterHandlerAndContinueNormalWorkflow() {
        WebHandler registerPostHandler = createMock(WebHandler)
        HttpServletRequest request = createMock(HttpServletRequest)
        HttpServletResponse response = createMock(HttpServletResponse)
        Form form = DefaultForm.builder().setFields(new ArrayList<Field>()).build()
        Client client = createMock(Client)
        Account account = createNiceMock(Account)
        CsrfTokenManager csrfTokenManager = createMock(CsrfTokenManager)
        RequestFieldValueResolver requestFieldValueResolver = createMock(RequestFieldValueResolver)
        Application application = createMock(Application)
        CustomData customData = createMock(CustomData)
        Publisher<RequestEvent> eventPublisher = createMock(Publisher)

        RegisterController registerController = new RegisterController(
                client: client,
                postRegisterHandler: registerPostHandler,
                csrfTokenManager: csrfTokenManager,
                fieldValueResolver: requestFieldValueResolver,
                produces: Arrays.asList(MediaType.TEXT_HTML),
                eventPublisher: eventPublisher
        )

        expect(client.instantiate(Account.class)).andReturn account
        expect(requestFieldValueResolver.getAllFields(request)).andReturn new HashMap<String, Object>()
        expect(request.getAttribute(Application.class.getName())).andReturn ((Application)application)
        expect(account.setGivenName("UNKNOWN")).andReturn account
        expect(account.setSurname("UNKNOWN")).andReturn account
        expect(account.getCustomData()).andReturn customData
        expect(application.createAccount(account)).andReturn account
        expect(registerPostHandler.handle(request, response, account)).andReturn true
        expect(eventPublisher.publish(anyObject()))

        expect(request.getAttribute(UserAgents.USER_AGENT_REQUEST_ATTRIBUTE_NAME)).andReturn new DefaultUserAgent(request)
        expect(request.getHeader("Accept")).andReturn "text/html"

        replay eventPublisher, registerPostHandler, request, response, client, requestFieldValueResolver, application, account

        def vm = registerController.onValidSubmit(request, response, form)

        verify eventPublisher, registerPostHandler, request, response, client, requestFieldValueResolver, application, account

        assertNotNull(vm, "ViewModel should not be empty")
    }

    @Test
    void testPostRegisterHandlerAndAbortNormalWorkflow() {
        WebHandler registerPostHandler = createMock(WebHandler)
        HttpServletRequest request = createMock(HttpServletRequest)
        HttpServletResponse response = createMock(HttpServletResponse)
        Form form = DefaultForm.builder().setFields(new ArrayList<Field>()).build()
        Client client = createMock(Client)
        Account account = createNiceMock(Account)
        CsrfTokenManager csrfTokenManager = createMock(CsrfTokenManager)
        RequestFieldValueResolver requestFieldValueResolver = createMock(RequestFieldValueResolver)
        Application application = createMock(Application)
        CustomData customData = createMock(CustomData)
        Publisher<RequestEvent> eventPublisher = createMock(Publisher)

        RegisterController registerController = new RegisterController(
                client: client,
                postRegisterHandler: registerPostHandler,
                csrfTokenManager: csrfTokenManager,
                fieldValueResolver: requestFieldValueResolver,
                produces: Arrays.asList(MediaType.TEXT_HTML),
                eventPublisher: eventPublisher
        )

        expect(client.instantiate(Account.class)).andReturn account
        expect(requestFieldValueResolver.getAllFields(request)).andReturn new HashMap<String, Object>()
        expect(request.getAttribute(Application.class.getName())).andReturn ((Application)application)
        expect(account.setGivenName("UNKNOWN")).andReturn account
        expect(account.setSurname("UNKNOWN")).andReturn account
        expect(account.getCustomData()).andReturn customData
        expect(application.createAccount(account)).andReturn account
        expect(registerPostHandler.handle(request, response, account)).andReturn false
        expect(eventPublisher.publish(anyObject()))

        replay eventPublisher, registerPostHandler, request, response, client, requestFieldValueResolver, application, account

        def vm = registerController.onValidSubmit(request, response, form)

        verify eventPublisher, registerPostHandler, request, response, client, requestFieldValueResolver, application, account

        assertNull(vm, "ViewModel should not be empty")
    }

    @Test
    void testAccountProperties() {
        //We need to be sure that every non-standard account property is properly considered to be custom data
        //Since there is now way to identify them automatically they have been hardcoded in RegisterController#ACCOUNT_PROPERTIES
        //This test checks that this list is accurate.
        //If the simple properties ever change in the account and this test fails then be sure to update RegisterController#ACCOUNT_PROPERTIES
        //in order to allow this test to pass.

        final List<String> NON_SIMPLE_PROPERTIES = Collections.unmodifiableList(Arrays.asList("fullName", "status", "customData", "emailVerificationToken", "directory", "tenant", "providerData", "groups", "groupMemberships", "apiKeys", "applications", "accessTokens", "refreshTokens"));

        def defaultAccount = new DefaultAccount(createStrictMock(InternalDataStore));
        def actualSimpleProperties = defaultAccount.PROPERTY_DESCRIPTORS
        for (String property : NON_SIMPLE_PROPERTIES) {
            actualSimpleProperties.remove(property)
        }

        actualSimpleProperties = actualSimpleProperties.keySet().asList()

        Assert.assertTrue(actualSimpleProperties.containsAll(RegisterController.ACCOUNT_PROPERTIES) && RegisterController.ACCOUNT_PROPERTIES.containsAll(actualSimpleProperties))
    }
}
