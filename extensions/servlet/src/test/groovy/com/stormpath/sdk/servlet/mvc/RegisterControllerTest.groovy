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
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.impl.account.DefaultAccount
import com.stormpath.sdk.impl.directory.DefaultDirectory
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.group.DefaultGroup
import com.stormpath.sdk.impl.organization.DefaultOrganization
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager
import com.stormpath.sdk.servlet.event.RequestEvent
import com.stormpath.sdk.servlet.event.impl.Publisher
import com.stormpath.sdk.servlet.form.DefaultForm
import com.stormpath.sdk.servlet.form.Field
import com.stormpath.sdk.servlet.form.Form
import com.stormpath.sdk.servlet.http.MediaType
import com.stormpath.sdk.servlet.http.UserAgents
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver
import com.stormpath.sdk.servlet.http.impl.DefaultUserAgent
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.anyObject
import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.createNiceMock
import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.partialMockBuilder
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull

/**
 * @since 1.0.0
 */
public class RegisterControllerTest {

    WebHandler registerPreHandler
    WebHandler registerPostHandler
    HttpServletRequest request
    HttpServletResponse response
    Form form
    Client client
    Account account
    CsrfTokenManager csrfTokenManager
    RequestFieldValueResolver requestFieldValueResolver
    Application application
    CustomData customData
    Publisher<RequestEvent> eventPublisher
    AccountStoreResolver accountStoreResolver
    Directory directory
    Organization organization
    Group group

    @BeforeMethod
    void setup() {
        registerPreHandler = createMock(WebHandler)
        registerPostHandler = createMock(WebHandler)
        request = createMock(HttpServletRequest)
        response = createMock(HttpServletResponse)
        form = DefaultForm.builder().setFields(new ArrayList<Field>()).build()
        client = createMock(Client)
        account = createNiceMock(Account)
        csrfTokenManager = createMock(CsrfTokenManager)
        requestFieldValueResolver = createMock(RequestFieldValueResolver)
        application = createMock(Application)
        customData = createMock(CustomData)
        eventPublisher = createMock(Publisher)
        accountStoreResolver = createMock(AccountStoreResolver)

        directory = partialMockBuilder(DefaultDirectory).addMockedMethod("createAccount", Account).createMock()
        organization = partialMockBuilder(DefaultOrganization).addMockedMethod("createAccount", Account).createMock()
        group = partialMockBuilder(DefaultGroup).createMock()
    }

    @Test
    void testAccountStoreResolverResolvesDirectory() {
        RegisterController registerController = new RegisterController(
                client: client,
                preRegisterHandler: registerPreHandler,
                csrfTokenManager: csrfTokenManager,
                fieldValueResolver: requestFieldValueResolver,
                produces: Arrays.asList(MediaType.TEXT_HTML),
                eventPublisher: eventPublisher,
                accountStoreResolver: accountStoreResolver
        )

        expect(client.instantiate(Account.class)).andReturn account
        expect(requestFieldValueResolver.getAllFields(request)).andReturn new HashMap<String, Object>()
        expect(request.getAttribute(Application.class.getName())).andReturn ((Application)application)
        expect(accountStoreResolver.getAccountStore(request, response)).andReturn directory
        expect(registerPreHandler.handle(request, response, account)).andReturn true
        expect(account.setGivenName("UNKNOWN")).andReturn account
        expect(account.setSurname("UNKNOWN")).andReturn account
        expect(account.getCustomData()).andReturn customData
        expect(directory.createAccount(account)).andReturn account
        expect(eventPublisher.publish(anyObject()))

        expect(request.getAttribute(UserAgents.USER_AGENT_REQUEST_ATTRIBUTE_NAME)).andReturn new DefaultUserAgent(request)
        expect(request.getHeader("Accept")).andReturn "text/html"

        replay eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver, directory

        def vm = registerController.onValidSubmit(request, response, form)

        verify eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver, directory

        assertNotNull(vm, "ViewModel should not be empty")
    }

    @Test(expectedExceptions = [IllegalStateException])
    void testAccountStoreResolverResolvesGroup() {
        RegisterController registerController = new RegisterController(
                client: client,
                preRegisterHandler: registerPreHandler,
                csrfTokenManager: csrfTokenManager,
                fieldValueResolver: requestFieldValueResolver,
                produces: Arrays.asList(MediaType.TEXT_HTML),
                eventPublisher: eventPublisher,
                accountStoreResolver: accountStoreResolver
        )

        expect(client.instantiate(Account.class)).andReturn account
        expect(requestFieldValueResolver.getAllFields(request)).andReturn new HashMap<String, Object>()
        expect(request.getAttribute(Application.class.getName())).andReturn ((Application)application)
        expect(accountStoreResolver.getAccountStore(request, response)).andReturn group
        expect(registerPreHandler.handle(request, response, account)).andReturn true
        expect(account.setGivenName("UNKNOWN")).andReturn account
        expect(account.setSurname("UNKNOWN")).andReturn account
        expect(account.getCustomData()).andReturn customData
        expect(directory.createAccount(account)).andReturn account
        expect(eventPublisher.publish(anyObject()))

        expect(request.getAttribute(UserAgents.USER_AGENT_REQUEST_ATTRIBUTE_NAME)).andReturn new DefaultUserAgent(request)
        expect(request.getHeader("Accept")).andReturn "text/html"

        replay eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver, group

        def vm = registerController.onValidSubmit(request, response, form)

        verify eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver, group

        assertNotNull(vm, "ViewModel should not be empty")
    }

    @Test
    void testAccountStoreResolverResolvesOrganization() {
        RegisterController registerController = new RegisterController(
                client: client,
                preRegisterHandler: registerPreHandler,
                csrfTokenManager: csrfTokenManager,
                fieldValueResolver: requestFieldValueResolver,
                produces: Arrays.asList(MediaType.TEXT_HTML),
                eventPublisher: eventPublisher,
                accountStoreResolver: accountStoreResolver
        )

        expect(client.instantiate(Account.class)).andReturn account
        expect(requestFieldValueResolver.getAllFields(request)).andReturn new HashMap<String, Object>()
        expect(request.getAttribute(Application.class.getName())).andReturn ((Application)application)
        expect(accountStoreResolver.getAccountStore(request, response)).andReturn organization
        expect(registerPreHandler.handle(request, response, account)).andReturn true
        expect(account.setGivenName("UNKNOWN")).andReturn account
        expect(account.setSurname("UNKNOWN")).andReturn account
        expect(account.getCustomData()).andReturn customData
        expect(organization.createAccount(account)).andReturn account
        expect(eventPublisher.publish(anyObject()))

        expect(request.getAttribute(UserAgents.USER_AGENT_REQUEST_ATTRIBUTE_NAME)).andReturn new DefaultUserAgent(request)
        expect(request.getHeader("Accept")).andReturn "text/html"

        replay eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver, organization

        def vm = registerController.onValidSubmit(request, response, form)

        verify eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver, organization

        assertNotNull(vm, "ViewModel should not be empty")
    }

    @Test
    void testPreRegisterHandlerAndContinueNormalWorkflow() {
        RegisterController registerController = new RegisterController(
                client: client,
                preRegisterHandler: registerPreHandler,
                csrfTokenManager: csrfTokenManager,
                fieldValueResolver: requestFieldValueResolver,
                produces: Arrays.asList(MediaType.TEXT_HTML),
                eventPublisher: eventPublisher,
                accountStoreResolver: accountStoreResolver
        )

        expect(client.instantiate(Account.class)).andReturn account
        expect(requestFieldValueResolver.getAllFields(request)).andReturn new HashMap<String, Object>()
        expect(request.getAttribute(Application.class.getName())).andReturn ((Application)application)
        expect(accountStoreResolver.getAccountStore(request, response)).andReturn null
        expect(registerPreHandler.handle(request, response, account)).andReturn true
        expect(account.setGivenName("UNKNOWN")).andReturn account
        expect(account.setSurname("UNKNOWN")).andReturn account
        expect(account.getCustomData()).andReturn customData
        expect(application.createAccount(account)).andReturn account
        expect(eventPublisher.publish(anyObject()))

        expect(request.getAttribute(UserAgents.USER_AGENT_REQUEST_ATTRIBUTE_NAME)).andReturn new DefaultUserAgent(request)
        expect(request.getHeader("Accept")).andReturn "text/html"

        replay eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver

        def vm = registerController.onValidSubmit(request, response, form)

        verify eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver

        assertNotNull(vm, "ViewModel should not be empty")
    }

    @Test
    void testPreRegisterHandlerAndAbortNormalWorkflow() {
        RegisterController registerController = new RegisterController(
                client: client,
                preRegisterHandler: registerPreHandler,
                csrfTokenManager: csrfTokenManager,
                fieldValueResolver: requestFieldValueResolver,
                produces: Arrays.asList(MediaType.TEXT_HTML),
                eventPublisher: eventPublisher,
                accountStoreResolver: accountStoreResolver
        )

        expect(client.instantiate(Account.class)).andReturn account
        expect(requestFieldValueResolver.getAllFields(request)).andReturn new HashMap<String, Object>()
        expect(request.getAttribute(Application.class.getName())).andReturn ((Application)application)
        expect(registerPreHandler.handle(request, response, account)).andReturn false
        expect(account.setGivenName("UNKNOWN")).andReturn account
        expect(account.setSurname("UNKNOWN")).andReturn account
        expect(account.getCustomData()).andReturn customData

        replay eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver

        def vm = registerController.onValidSubmit(request, response, form)

        verify eventPublisher, registerPreHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver

        assertNull(vm, "ViewModel should be empty")
    }

    @Test
    void testPostRegisterHandlerAndContinueNormalWorkflow() {
        RegisterController registerController = new RegisterController(
                client: client,
                postRegisterHandler: registerPostHandler,
                csrfTokenManager: csrfTokenManager,
                fieldValueResolver: requestFieldValueResolver,
                produces: Arrays.asList(MediaType.TEXT_HTML),
                eventPublisher: eventPublisher,
                accountStoreResolver: accountStoreResolver
        )

        expect(client.instantiate(Account.class)).andReturn account
        expect(requestFieldValueResolver.getAllFields(request)).andReturn new HashMap<String, Object>()
        expect(request.getAttribute(Application.class.getName())).andReturn ((Application)application)
        expect(accountStoreResolver.getAccountStore(request, response)).andReturn null
        expect(account.setGivenName("UNKNOWN")).andReturn account
        expect(account.setSurname("UNKNOWN")).andReturn account
        expect(account.getCustomData()).andReturn customData
        expect(application.createAccount(account)).andReturn account
        expect(registerPostHandler.handle(request, response, account)).andReturn true
        expect(eventPublisher.publish(anyObject()))

        expect(request.getAttribute(UserAgents.USER_AGENT_REQUEST_ATTRIBUTE_NAME)).andReturn new DefaultUserAgent(request)
        expect(request.getHeader("Accept")).andReturn "text/html"

        replay eventPublisher, registerPostHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver

        def vm = registerController.onValidSubmit(request, response, form)

        verify eventPublisher, registerPostHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver

        assertNotNull(vm, "ViewModel should not be empty")
    }

    @Test
    void testPostRegisterHandlerAndAbortNormalWorkflow() {
        RegisterController registerController = new RegisterController(
                client: client,
                postRegisterHandler: registerPostHandler,
                csrfTokenManager: csrfTokenManager,
                fieldValueResolver: requestFieldValueResolver,
                produces: Arrays.asList(MediaType.TEXT_HTML),
                eventPublisher: eventPublisher,
                accountStoreResolver: accountStoreResolver
        )

        expect(client.instantiate(Account.class)).andReturn account
        expect(requestFieldValueResolver.getAllFields(request)).andReturn new HashMap<String, Object>()
        expect(request.getAttribute(Application.class.getName())).andReturn ((Application)application)
        expect(accountStoreResolver.getAccountStore(request, response)).andReturn null
        expect(account.setGivenName("UNKNOWN")).andReturn account
        expect(account.setSurname("UNKNOWN")).andReturn account
        expect(account.getCustomData()).andReturn customData
        expect(application.createAccount(account)).andReturn account
        expect(registerPostHandler.handle(request, response, account)).andReturn false
        expect(eventPublisher.publish(anyObject()))

        replay eventPublisher, registerPostHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver

        def vm = registerController.onValidSubmit(request, response, form)

        verify eventPublisher, registerPostHandler, request, response, client, requestFieldValueResolver, application, account, accountStoreResolver

        assertNull(vm, "ViewModel should not be empty")
    }

    @Test
    void testAccountProperties() {
        //We need to be sure that every non-standard account property is properly considered to be custom data
        //Since there is now way to identify them automatically they have been hardcoded in RegisterController#ACCOUNT_PROPERTIES
        //This test checks that this list is accurate.
        //If the simple properties ever change in the account and this test fails then be sure to update RegisterController#ACCOUNT_PROPERTIES
        //in order to allow this test to pass.

        final List<String> NON_SIMPLE_PROPERTIES = Collections.unmodifiableList(Arrays.asList(
                "fullName", "status", "customData", "emailVerificationToken", "directory", "tenant",
                "providerData", "groups", "groupMemberships", "apiKeys", "applications", "accessTokens",
                "refreshTokens", "accountLinks", "linkedAccounts"));

        def defaultAccount = new DefaultAccount(createStrictMock(InternalDataStore));
        def actualSimpleProperties = defaultAccount.PROPERTY_DESCRIPTORS
        for (String property : NON_SIMPLE_PROPERTIES) {
            actualSimpleProperties.remove(property)
        }

        actualSimpleProperties = actualSimpleProperties.keySet().asList()

        Assert.assertTrue(actualSimpleProperties.containsAll(RegisterController.ACCOUNT_PROPERTIES) && RegisterController.ACCOUNT_PROPERTIES.containsAll(actualSimpleProperties))
    }
}
