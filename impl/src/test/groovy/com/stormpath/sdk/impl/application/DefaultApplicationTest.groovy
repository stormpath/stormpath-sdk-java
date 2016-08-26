/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.application

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountCriteria
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.account.CreateAccountRequest
import com.stormpath.sdk.account.PasswordResetToken
import com.stormpath.sdk.account.VerificationEmailRequest
import com.stormpath.sdk.api.ApiKeys
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationAccountStoreMapping
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList
import com.stormpath.sdk.application.ApplicationStatus
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.authc.UsernamePasswordRequests
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.directory.DirectoryCriteria
import com.stormpath.sdk.group.CreateGroupRequest
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupCriteria
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.group.GroupOptions
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.impl.account.DefaultAccountList
import com.stormpath.sdk.impl.account.DefaultPasswordResetToken
import com.stormpath.sdk.impl.account.DefaultVerificationEmailRequest
import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials
import com.stormpath.sdk.impl.authc.BasicLoginAttempt
import com.stormpath.sdk.impl.authc.DefaultBasicLoginAttempt
import com.stormpath.sdk.impl.cache.DefaultCacheManager
import com.stormpath.sdk.impl.directory.DefaultCustomData
import com.stormpath.sdk.impl.directory.DefaultDirectory
import com.stormpath.sdk.impl.ds.DefaultDataStore
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.ds.JacksonMapMarshaller
import com.stormpath.sdk.impl.group.DefaultGroupList
import com.stormpath.sdk.impl.http.Request
import com.stormpath.sdk.impl.http.RequestExecutor
import com.stormpath.sdk.impl.http.Response
import com.stormpath.sdk.impl.http.support.DefaultRequest
import com.stormpath.sdk.impl.idsite.DefaultIdSiteUrlBuilder
import com.stormpath.sdk.impl.provider.DefaultProviderAccountAccess
import com.stormpath.sdk.impl.provider.ProviderAccountAccess
import com.stormpath.sdk.impl.resource.CollectionReference
import com.stormpath.sdk.impl.resource.ListProperty
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StatusProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.impl.saml.DefaultSamlPolicy
import com.stormpath.sdk.impl.tenant.DefaultTenant
import com.stormpath.sdk.lang.Objects
import com.stormpath.sdk.oauth.OAuthPolicy
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.provider.FacebookProviderData
import com.stormpath.sdk.provider.GithubProviderData
import com.stormpath.sdk.provider.ProviderAccountRequest
import com.stormpath.sdk.provider.ProviderAccountResult
import com.stormpath.sdk.provider.Providers
import com.stormpath.sdk.resource.Resource
import com.stormpath.sdk.saml.SamlPolicy
import com.stormpath.sdk.tenant.Tenant
import org.easymock.EasyMock
import org.easymock.IArgumentMatcher
import org.testng.annotations.Test

import java.text.DateFormat

import static org.easymock.EasyMock.*
import static org.powermock.api.easymock.PowerMock.createStrictMock
import static org.testng.Assert.*

/**
 * @since 0.8
 */
class DefaultApplicationTest {

    @Test
    void testGetPropertyDescriptors() {

        def defaultApplication = new DefaultApplication(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultApplication.getPropertyDescriptors()

        assertEquals( propertyDescriptors.size(), 14)

        assertTrue(propertyDescriptors.get("name") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("description") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("status") instanceof StatusProperty && propertyDescriptors.get("status").getType().equals(ApplicationStatus))
        assertTrue(propertyDescriptors.get("tenant") instanceof ResourceReference && propertyDescriptors.get("tenant").getType().equals(Tenant))
        assertTrue(propertyDescriptors.get("defaultAccountStoreMapping") instanceof ResourceReference && propertyDescriptors.get("defaultAccountStoreMapping").getType().equals(ApplicationAccountStoreMapping))
        assertTrue(propertyDescriptors.get("defaultGroupStoreMapping") instanceof ResourceReference && propertyDescriptors.get("defaultGroupStoreMapping").getType().equals(ApplicationAccountStoreMapping))
        assertTrue(propertyDescriptors.get("accounts") instanceof CollectionReference && propertyDescriptors.get("accounts").getType().equals(AccountList))
        assertTrue(propertyDescriptors.get("groups") instanceof CollectionReference && propertyDescriptors.get("groups").getType().equals(GroupList))
        assertTrue(propertyDescriptors.get("passwordResetTokens") instanceof CollectionReference && propertyDescriptors.get("passwordResetTokens").getType().equals(PasswordResetTokenList))
        assertTrue(propertyDescriptors.get("accountStoreMappings") instanceof CollectionReference && propertyDescriptors.get("accountStoreMappings").getType().equals(ApplicationAccountStoreMappingList))
        //since 1.0.0
        assertTrue(propertyDescriptors.get("customData") instanceof ResourceReference && propertyDescriptors.get("customData").getType().equals(CustomData))
        //since 1.0.RC7
        assertTrue(propertyDescriptors.get("oAuthPolicy") instanceof ResourceReference && propertyDescriptors.get("oAuthPolicy").getType().equals(OAuthPolicy))
        //since 1.0.RC8
        assertTrue(propertyDescriptors.get("samlPolicy") instanceof ResourceReference && propertyDescriptors.get("samlPolicy").getType().equals(SamlPolicy))
        assertTrue(propertyDescriptors.get("authorizedCallbackUris") instanceof ListProperty)
    }

    @Test
    void testMethods() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"],
                createdAt: "2015-01-01T00:00:00Z",
                modifiedAt: "2015-02-01T00:00:00Z",
                samlPolicy: [href: "https://api.stormpath.com/v1/samlPolicies/5DxyJhixTppxnW8b6nJlpx"]]

        def internalDataStore = createStrictMock(InternalDataStore)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)

        assertNull(defaultApplication.getStatus())

        defaultApplication = defaultApplication.setStatus(ApplicationStatus.DISABLED)
            .setName("App Name")
            .setDescription("App Description")

        assertEquals(defaultApplication.getStatus(), ApplicationStatus.DISABLED)
        assertEquals(defaultApplication.getName(), "App Name")
        assertEquals(defaultApplication.getDescription(), "App Description")

        DateFormat df = new ISO8601DateFormat();
        assertEquals(df.format(defaultApplication.getCreatedAt()), "2015-01-01T00:00:00Z")
        assertEquals(df.format(defaultApplication.getModifiedAt()), "2015-02-01T00:00:00Z")

        expect(internalDataStore.instantiate(GroupList, properties.groups)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        def groupCriteria = createStrictMock(GroupCriteria)
        expect(internalDataStore.instantiate(GroupList, properties.groups)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))
        expect(internalDataStore.getResource(properties.groups.href, GroupList, groupCriteria)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        def groupCriteriaMap = [name: "some+search"]
        expect(internalDataStore.instantiate(GroupList, properties.groups)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))
        expect(internalDataStore.getResource(properties.groups.href, GroupList, groupCriteriaMap)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        expect(internalDataStore.instantiate(AccountList, properties.accounts)).andReturn(new DefaultAccountList(internalDataStore, properties.accounts))

        def accountCriteria = createStrictMock(AccountCriteria)
        expect(internalDataStore.instantiate(AccountList, properties.accounts)).andReturn(new DefaultAccountList(internalDataStore, properties.accounts))
        expect(internalDataStore.getResource(properties.accounts.href, AccountList, accountCriteria)).andReturn(new DefaultAccountList(internalDataStore, properties.accounts))

        def accountCriteriaMap = [surname: "some+search"]
        expect(internalDataStore.instantiate(AccountList, properties.accounts)).andReturn(new DefaultAccountList(internalDataStore, properties.accounts))
        expect(internalDataStore.getResource(properties.accounts.href, AccountList, accountCriteriaMap)).andReturn(new DefaultAccountList(internalDataStore, properties.accounts))

        expect(internalDataStore.instantiate(Tenant, properties.tenant)).andReturn(new DefaultTenant(internalDataStore, properties.tenant))

        expect(internalDataStore.instantiate(SamlPolicy, properties.samlPolicy)).andReturn(new DefaultSamlPolicy(internalDataStore, properties.samlPolicy))

        expect(internalDataStore.delete(defaultApplication))

        def account = createStrictMock(Account)
        def innerProperties = [href: properties.passwordResetTokens.href + "/bwehiuwehfiwuh4huj",
                account: [href: "https://api.stormpath.com/v1/accounts/wewjheu824rWEFEjgy"]]
        def defaultPassResetToken = new DefaultPasswordResetToken(internalDataStore)
        expect(internalDataStore.instantiate(PasswordResetToken)).andReturn(defaultPassResetToken)
        expect(internalDataStore.create(properties.passwordResetTokens.href, defaultPassResetToken)).andReturn(new DefaultPasswordResetToken(internalDataStore, innerProperties))
        expect(internalDataStore.instantiate(Account, innerProperties.account)).andReturn(account)

        expect(internalDataStore.instantiate(PasswordResetToken, [href: properties.passwordResetTokens.href + "/token"])) andReturn(new DefaultPasswordResetToken(internalDataStore, innerProperties))
        expect(internalDataStore.instantiate(Account, innerProperties.account)).andReturn(account)

        def authenticationResult01 = createStrictMock(AuthenticationResult)
        def defaultBasicLoginAttempt = new DefaultBasicLoginAttempt(internalDataStore)
        expect(internalDataStore.instantiate(BasicLoginAttempt)).andReturn(defaultBasicLoginAttempt)
        defaultBasicLoginAttempt.setType("basic")
        defaultBasicLoginAttempt.setValue("dXNlcm5hbWU6cGFzc3dvcmQ=")
        expect(internalDataStore.create((String) properties.href + "/loginAttempts", defaultBasicLoginAttempt, AuthenticationResult)).andReturn(authenticationResult01)

        def authenticationResult02 = createStrictMock(AuthenticationResult)
        defaultBasicLoginAttempt = new DefaultBasicLoginAttempt(internalDataStore)
        expect(internalDataStore.instantiate(BasicLoginAttempt)).andReturn(defaultBasicLoginAttempt)
        defaultBasicLoginAttempt.setType("basic")
        defaultBasicLoginAttempt.setValue("dXNlcm5hbWU6cGFzc3dvcmQ=")

        def options = UsernamePasswordRequests.options().withAccount()
         expect(internalDataStore.create((String) properties.href + "/loginAttempts", defaultBasicLoginAttempt, AuthenticationResult.class, options)).andReturn(authenticationResult02)

        replay internalDataStore, groupCriteria, accountCriteria, account

        def resource = defaultApplication.getGroups()
        assertTrue(resource instanceof DefaultGroupList && resource.getHref().equals(properties.groups.href))

        resource = defaultApplication.getGroups(groupCriteria)
        assertTrue(resource instanceof DefaultGroupList && resource.getHref().equals(properties.groups.href))

        resource = defaultApplication.getGroups(groupCriteriaMap)
        assertTrue(resource instanceof DefaultGroupList && resource.getHref().equals(properties.groups.href))

        resource = defaultApplication.getAccounts()
        assertTrue(resource instanceof DefaultAccountList && resource.getHref().equals(properties.accounts.href))

        resource = defaultApplication.getAccounts(accountCriteria)
        assertTrue(resource instanceof DefaultAccountList && resource.getHref().equals(properties.accounts.href))

        resource = defaultApplication.getAccounts(accountCriteriaMap)
        assertTrue(resource instanceof DefaultAccountList && resource.getHref().equals(properties.accounts.href))

        resource = defaultApplication.getTenant()
        assertTrue(resource instanceof DefaultTenant && resource.getHref().equals(properties.tenant.href))

        resource = defaultApplication.getSamlPolicy()
        assertTrue(resource instanceof SamlPolicy && resource.getHref().equals(properties.samlPolicy.href))

        defaultApplication.delete()

        assertEquals(defaultApplication.sendPasswordResetEmail("some@email.com").getAccount(), account)
        assertEquals(defaultApplication.verifyPasswordResetToken("token"), account)
        assertEquals(defaultApplication.authenticateAccount(UsernamePasswordRequests.builder().setUsernameOrEmail("username").setPassword("password").build()), authenticationResult01)

        def request = UsernamePasswordRequests.builder().setUsernameOrEmail("username").setPassword("password").withResponseOptions(options).build()
        assertEquals(defaultApplication.authenticateAccount(request), authenticationResult02)

        verify internalDataStore, groupCriteria, accountCriteria, account
    }

    @Test
    void testSendPasswordResetEmailWithAccountStore() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                          tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                          accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                          groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                          passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"]]

        def internalDataStore = createStrictMock(InternalDataStore)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)

        def email = 'foo@bar.com'
        def account = createStrictMock(Account)
        def accountStore = createStrictMock(Directory)
        def accountStoreHref = 'https://api.stormpath.com/v1/directories/dir123'
        def innerProperties = [href: properties.passwordResetTokens.href + "/bwehiuwehfiwuh4huj",
                               account: [href: "https://api.stormpath.com/v1/accounts/wewjheu824rWEFEjgy"]]
        def defaultPassResetToken = new DefaultPasswordResetToken(internalDataStore)

        expect(internalDataStore.instantiate(PasswordResetToken)).andReturn(defaultPassResetToken)
        expect(internalDataStore.create(properties.passwordResetTokens.href, defaultPassResetToken)).andReturn(new DefaultPasswordResetToken(internalDataStore, innerProperties))
        expect(accountStore.getHref()).andReturn(accountStoreHref)
        expect(internalDataStore.instantiate(Account, innerProperties.account)).andReturn(account)

        replay internalDataStore, account, accountStore

        def returnedToken = defaultApplication.sendPasswordResetEmail(email, accountStore)

        assertEquals(returnedToken.getAccount(), account)

        assertEquals(defaultPassResetToken.dirtyProperties.accountStore.href, accountStoreHref)

        verify internalDataStore, account, accountStore
    }

    @Test
    void testCreateAccountWithDefaultCreateAccountRequest() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"]]

        def internalDataStore = createStrictMock(InternalDataStore)

        def request = createStrictMock(CreateAccountRequest)
        def account = createStrictMock(Account)
        def accountList = createStrictMock(AccountList)
        def returnedAccount = createStrictMock(Account)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)

        expect(request.getAccount()).andReturn(account)
        expect(request.isRegistrationWorkflowOptionSpecified()).andReturn(false)
        expect(request.isPasswordFormatSpecified()).andReturn(false)
        expect(request.isAccountOptionsSpecified()).andReturn(false)
        expect(internalDataStore.instantiate(AccountList, [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"])).andReturn(accountList)
        expect(accountList.getHref()).andReturn("https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts")

        expect(internalDataStore.create("https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts", account)).andReturn(returnedAccount)

        replay internalDataStore, request, account, accountList, returnedAccount

        assertEquals(defaultApplication.createAccount(request), returnedAccount)

        verify internalDataStore, request, account, accountList, returnedAccount
    }

    @Test
    void testCreateAccountWithWorkflowFalse() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"]]

        def internalDataStore = createStrictMock(InternalDataStore)
        def request = createStrictMock(CreateAccountRequest)
        def accountCriteria = createStrictMock(AccountCriteria)
        def account = createStrictMock(Account)
        def accountList = createStrictMock(AccountList)
        def returnedAccount = createStrictMock(Account)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)

        expect(request.getAccount()).andReturn(account)
        expect(request.isRegistrationWorkflowOptionSpecified()).andReturn(true)
        expect(request.isRegistrationWorkflowEnabled()).andReturn(false)
        expect(request.isPasswordFormatSpecified()).andReturn(false)
        expect(request.isAccountOptionsSpecified()).andReturn(true)
        expect(request.getAccountOptions()).andReturn(accountCriteria)
        expect(internalDataStore.instantiate(AccountList, [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"])).andReturn(accountList)
        expect(accountList.getHref()).andReturn("https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts")

        expect(internalDataStore.create("https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts?registrationWorkflowEnabled=false", account, accountCriteria)).andReturn(returnedAccount)

        replay internalDataStore, request, account, accountList, returnedAccount

        assertEquals(defaultApplication.createAccount(request), returnedAccount)

        verify internalDataStore, request, account, accountList, returnedAccount
    }

    @Test
    void testCreateAccount() {

        def account = createStrictMock(Account)
        def partiallyMockedDefaultApplication = createMockBuilder(DefaultApplication.class)
                .addMockedMethod("createAccount", CreateAccountRequest).createMock();

        expect(partiallyMockedDefaultApplication.createAccount((CreateAccountRequest) EasyMock.anyObject())).andReturn(account)

        replay partiallyMockedDefaultApplication, account

        assertEquals(partiallyMockedDefaultApplication.createAccount(account), account)

        verify partiallyMockedDefaultApplication, account
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testCreateGroupWithNullArgument() {
        def app = new DefaultApplication(createStrictMock(InternalDataStore))

        app.createGroup((Group) null)
    }

    @Test
    void testCreateGroup() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"]]

        def dataStore = createStrictMock(InternalDataStore)
        def groupList = createStrictMock(GroupList)
        def group = createStrictMock(Group)

        def app = new DefaultApplication(dataStore, properties)

        expect(dataStore.instantiate(eq(GroupList.class), eq([href:properties.groups.href]))).andReturn(groupList)
        expect(groupList.getHref()).andReturn(properties.groups.href)

        expect(dataStore.create(eq(properties.groups.href), same(group))).andReturn(group)

        replay dataStore, group, groupList

        def returnedGroup = app.createGroup(group)

        assertSame returnedGroup, group

        verify dataStore, group, groupList
    }

    @Test
    void testCreateGroupWithGroupCriteria() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"]]

        def dataStore = createStrictMock(InternalDataStore)
        def request = createStrictMock(CreateGroupRequest)
        def groupList = createStrictMock(GroupList)
        def group = createStrictMock(Group)
        def groupOptions = createStrictMock(GroupOptions)

        def app = new DefaultApplication(dataStore, properties)

        expect(dataStore.instantiate(eq(GroupList.class), eq([href:properties.groups.href]))).andReturn(groupList)
        expect(groupList.getHref()).andReturn(properties.groups.href)

        expect(request.getGroup()).andReturn(group)
        expect(request.isGroupOptionsSpecified()).andReturn(true)
        expect(request.getGroupOptions()).andReturn(groupOptions)

        expect(dataStore.create(eq(properties.groups.href), same(group), same(groupOptions))).andReturn(group)

        replay dataStore, group, groupList, request

        def returnedGroup = app.createGroup(request)

        assertSame returnedGroup, group

        verify dataStore, group, groupList, request
    }

    @Test
    void testCreateGroupWithoutGroupCriteria() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"]]

        def dataStore = createStrictMock(InternalDataStore)
        def request = createStrictMock(CreateGroupRequest)
        def groupList = createStrictMock(GroupList)
        def group = createStrictMock(Group)

        def app = new DefaultApplication(dataStore, properties)

        expect(dataStore.instantiate(eq(GroupList.class), eq([href:properties.groups.href]))).andReturn(groupList)
        expect(groupList.getHref()).andReturn(properties.groups.href)

        expect(request.getGroup()).andReturn(group)
        expect(request.isGroupOptionsSpecified()).andReturn(false)

        expect(dataStore.create(eq(properties.groups.href), same(group))).andReturn(group)

        replay dataStore, group, groupList, request

        def returnedGroup = app.createGroup(request)

        assertSame returnedGroup, group

        verify dataStore, group, groupList, request
    }

    //@since 1.0.RC
    @Test
    void testSetDefaultAccountStore() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"],
                defaultAccountStoreMapping: [href: "https://api.stormpath.com/v1/accountStoreMappings/5dc0HbVMB8g3GWpSkOzqfF"],
                defaultGroupStoreMapping: [href: "https://api.stormpath.com/v1/accountStoreMappings/5dc0HbVMB8g3GWpSkOzqfF"],
                accountStoreMappings: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accountStoreMappings"],
                customData: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/customData"]
        ]

        def accountStoreHref = "https://api.stormpath.com/v1/directories/6i2DiJWcsG6ZyUA8r0EwQU"
        def groupHref = "https://api.stormpath.com/v1/groups/6dWPIXEi4MvGTnQcWApizf"

        def dataStore = createStrictMock(InternalDataStore)
        def accountStore = createStrictMock(AccountStore)
        def group = createStrictMock(Group)
        def accountStoreMappings = createStrictMock(ApplicationAccountStoreMappingList)
        def iterator = createStrictMock(Iterator)
        def accountStoreMapping = createStrictMock(ApplicationAccountStoreMapping)
        def newAccountStoreMapping = createStrictMock(ApplicationAccountStoreMapping)
        def customData = new DefaultCustomData(dataStore)
        def requestExecutor = createStrictMock(RequestExecutor)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)
        def internalDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials)

        expect(dataStore.instantiate(CustomData, properties.customData)).andReturn(customData)
        expect(dataStore.instantiate(ApplicationAccountStoreMappingList, properties.accountStoreMappings)).andReturn(accountStoreMappings)
        expect(accountStoreMappings.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountStoreMapping)
        expect(accountStoreMapping.getAccountStore()).andReturn(accountStore)
        expect(accountStore.getHref()).andReturn(accountStoreHref)
        expect(group.getHref()).andReturn(groupHref)
        expect(iterator.hasNext()).andReturn(false)
        expect(dataStore.instantiate(ApplicationAccountStoreMapping)).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.setAccountStore(group)).andReturn(newAccountStoreMapping)

        def newPropertiesState = new LinkedHashMap<String, Object>()
        newPropertiesState.putAll(properties)
        newPropertiesState.put("customData", customData);
        def modifiedApp = new DefaultApplication(internalDataStore, newPropertiesState)

        expect(newAccountStoreMapping.setApplication((Application) reportMatcher(new ApplicationMatcher(modifiedApp)))).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.setListIndex(Integer.MAX_VALUE)).andReturn(newAccountStoreMapping)
        expect(dataStore.create("/accountStoreMappings", newAccountStoreMapping)).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.setDefaultAccountStore(true)).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.save())

        modifiedApp = new DefaultApplication(internalDataStore, newPropertiesState)
        expect(dataStore.save((Application) reportMatcher(new ApplicationMatcher(modifiedApp))))

        //Second execution
        expect(dataStore.instantiate(ApplicationAccountStoreMappingList, properties.accountStoreMappings)).andReturn(accountStoreMappings)
        expect(accountStoreMappings.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountStoreMapping)
        expect(accountStoreMapping.getAccountStore()).andReturn(accountStore)
        expect(accountStore.getHref()).andReturn(accountStoreHref) times 2
        expect(accountStoreMapping.setDefaultAccountStore(true)).andReturn(accountStoreMapping)
        expect(accountStoreMapping.save())

        modifiedApp = new DefaultApplication(internalDataStore, newPropertiesState)
        expect(dataStore.save((Application) reportMatcher(new ApplicationMatcher(modifiedApp))))

        replay dataStore, accountStore, group, accountStoreMappings, iterator, accountStoreMapping, newAccountStoreMapping, apiKeyCredentials, requestExecutor

        def app = new DefaultApplication(dataStore, properties)
        app.setDefaultAccountStore(group)
        app.setDefaultAccountStore(accountStore)

        verify dataStore, accountStore, group, accountStoreMappings, iterator, accountStoreMapping, newAccountStoreMapping, apiKeyCredentials, requestExecutor
    }

    //@since 1.0.RC
    @Test
    void testSetDefaultGroupStore() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"],
                defaultAccountStoreMapping: [href: "https://api.stormpath.com/v1/accountStoreMappings/5dc0HbVMB8g3GWpSkOzqfF"],
                defaultGroupStoreMapping: [href: "https://api.stormpath.com/v1/accountStoreMappings/5dc0HbVMB8g3GWpSkOzqfF"],
                accountStoreMappings: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accountStoreMappings"],
                customData: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/customData"]
        ]

        def accountStoreHref = "https://api.stormpath.com/v1/directories/6i2DiJWcsG6ZyUA8r0EwQU"
        def groupHref = "https://api.stormpath.com/v1/groups/6dWPIXEi4MvGTnQcWApizf"

        def dataStore = createStrictMock(InternalDataStore)
        def accountStore = createStrictMock(AccountStore)
        def group = createStrictMock(Group)
        def accountStoreMappings = createStrictMock(ApplicationAccountStoreMappingList)
        def iterator = createStrictMock(Iterator)
        def accountStoreMapping = createStrictMock(ApplicationAccountStoreMapping)
        def newAccountStoreMapping = createStrictMock(ApplicationAccountStoreMapping)
        def customData = new DefaultCustomData(dataStore)
        def requestExecutor = createStrictMock(RequestExecutor)
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)
        def internalDataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials)

        expect(dataStore.instantiate(CustomData, properties.customData)).andReturn(customData)
        expect(dataStore.instantiate(ApplicationAccountStoreMappingList, properties.accountStoreMappings)).andReturn(accountStoreMappings)
        expect(accountStoreMappings.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountStoreMapping)
        expect(accountStoreMapping.getAccountStore()).andReturn(accountStore)
        expect(accountStore.getHref()).andReturn(accountStoreHref)
        expect(group.getHref()).andReturn(groupHref)
        expect(iterator.hasNext()).andReturn(false)
        expect(dataStore.instantiate(ApplicationAccountStoreMapping)).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.setAccountStore(group)).andReturn(newAccountStoreMapping)

        def newPropertiesState = new LinkedHashMap<String, Object>()
        newPropertiesState.putAll(properties)
        newPropertiesState.put("customData", customData);
        def modifiedApp = new DefaultApplication(internalDataStore, newPropertiesState)

        expect(newAccountStoreMapping.setApplication((Application) reportMatcher(new ApplicationMatcher(modifiedApp)))).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.setListIndex(Integer.MAX_VALUE)).andReturn(newAccountStoreMapping)
        expect(dataStore.create("/accountStoreMappings", newAccountStoreMapping)).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.setDefaultGroupStore(true)).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.save())

        modifiedApp = new DefaultApplication(internalDataStore, newPropertiesState)
        expect(dataStore.save((Application) reportMatcher(new ApplicationMatcher(modifiedApp))))

        //Second execution
        expect(dataStore.instantiate(ApplicationAccountStoreMappingList, properties.accountStoreMappings)).andReturn(accountStoreMappings)
        expect(accountStoreMappings.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountStoreMapping)
        expect(accountStoreMapping.getAccountStore()).andReturn(accountStore)
        expect(accountStore.getHref()).andReturn(accountStoreHref) times 2
        expect(accountStoreMapping.setDefaultGroupStore(true)).andReturn(accountStoreMapping)
        expect(accountStoreMapping.save())

        modifiedApp = new DefaultApplication(internalDataStore, newPropertiesState)
        expect(dataStore.save((Application) reportMatcher(new ApplicationMatcher(modifiedApp))))

        replay dataStore, accountStore, group, accountStoreMappings, iterator, accountStoreMapping, newAccountStoreMapping, apiKeyCredentials, requestExecutor

        def app = new DefaultApplication(dataStore, properties)
        app.setDefaultGroupStore(group)
        app.setDefaultGroupStore(accountStore)

        verify dataStore, accountStore, group, accountStoreMappings, iterator, accountStoreMapping, newAccountStoreMapping, apiKeyCredentials, requestExecutor
    }

    //@since 1.0.beta
    @Test
    void testGetAccount() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"]]

        def internalDataStore = createStrictMock(InternalDataStore)
        def providerAccountResult = createStrictMock(ProviderAccountResult)
        ProviderAccountRequest request = Providers.FACEBOOK.account().setAccessToken("CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD").build()

        def providerAccountAccess = new DefaultProviderAccountAccess<FacebookProviderData>(internalDataStore);
        providerAccountAccess.setProviderData(request.getProviderData())

        expect(internalDataStore.create(eq(properties.accounts.href), (Resource) reportMatcher(new ProviderAccountAccessEquals(providerAccountAccess)), (Class)eq(ProviderAccountResult))).andReturn(providerAccountResult)

        replay(internalDataStore, providerAccountResult)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)
        ProviderAccountResult accountResult = defaultApplication.getAccount(request)
        assertNotNull(accountResult)

        verify(internalDataStore, providerAccountResult)
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testCreateSsoRedirectUrl() {
        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj"]

        def internalDataStore = createStrictMock(InternalDataStore)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)
        def ssoRedirectUrlBuilder = defaultApplication.newIdSiteUrlBuilder()

        assertTrue(ssoRedirectUrlBuilder instanceof DefaultIdSiteUrlBuilder)
        assertEquals(ssoRedirectUrlBuilder.internalDataStore, internalDataStore)
        assertEquals(ssoRedirectUrlBuilder.applicationHref, properties.href)
    }

    /**
     * Testing fix for https://github.com/stormpath/stormpath-sdk-java/issues/184
     * @since 1.0.RC4.2
     */
    @Test
    void testCreateSsoRedirectUrlNotHardcoded() {
        def properties = [href: "https://enterprise.stormpath.com/v1/applications/jefoifj93riu23ioj"]

        def internalDataStore = createStrictMock(InternalDataStore)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)
        def ssoRedirectUrlBuilder = defaultApplication.newIdSiteUrlBuilder()

        assertEquals(ssoRedirectUrlBuilder.ssoEndpoint, properties.href.substring(0, properties.href.indexOf("/", 8)) + "/sso")
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testResetPassword() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"]]

        def internalDataStore = createStrictMock(InternalDataStore)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)


        def account = createStrictMock(Account)
        def innerProperties = [href: properties.passwordResetTokens.href + "/bwehiuwehfiwuh4huj",
                account: [href: "https://api.stormpath.com/v1/accounts/wewjheu824rWEFEjgy"]]
        def defaultPassResetToken = new DefaultPasswordResetToken(internalDataStore)
        expect(internalDataStore.instantiate(PasswordResetToken)).andReturn(defaultPassResetToken)
        expect(internalDataStore.create(properties.passwordResetTokens.href, defaultPassResetToken)).andReturn(new DefaultPasswordResetToken(internalDataStore, innerProperties))
        expect(internalDataStore.instantiate(Account, innerProperties.account)).andReturn(account)

        def instantiatedPasswordResetToken = new DefaultPasswordResetToken(internalDataStore, innerProperties)
        expect(internalDataStore.instantiate(PasswordResetToken, [href: properties.passwordResetTokens.href + "/token"])) andReturn(instantiatedPasswordResetToken)

        def createdPasswordResetToken = new DefaultPasswordResetToken(internalDataStore, [account: account])
        expect(internalDataStore.create(properties.passwordResetTokens.href + "/token", instantiatedPasswordResetToken, PasswordResetToken)) andReturn(createdPasswordResetToken)

        def authenticationResult = createStrictMock(AuthenticationResult)
        def defaultBasicLoginAttempt = new DefaultBasicLoginAttempt(internalDataStore)
        expect(internalDataStore.instantiate(BasicLoginAttempt)).andReturn(defaultBasicLoginAttempt)
        defaultBasicLoginAttempt.setType("basic")
        defaultBasicLoginAttempt.setValue("dXNlcm5hbWU6cGFzc3dvcmQ=")

        expect(internalDataStore.create(properties.href + "/loginAttempts", defaultBasicLoginAttempt, AuthenticationResult)).andReturn(authenticationResult)

        replay internalDataStore, account

        assertEquals(defaultApplication.sendPasswordResetEmail("some@email.com").getAccount(), account)
        assertEquals(defaultApplication.resetPassword("token", "myNewPassword"), account)
        assertEquals(defaultApplication.authenticateAccount(UsernamePasswordRequests.builder().setUsernameOrEmail("username").setPassword("myNewPassword").build()), authenticationResult, null)

        verify internalDataStore, account
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testResetPasswordInvalidParameters() {

        def defaultApplication = new DefaultApplication(null)

        try {
            defaultApplication.resetPassword("", "myNewPassword")
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "passwordResetToken cannot be empty or null.")
        }

        try {
            defaultApplication.resetPassword(null, "myNewPassword")
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "passwordResetToken cannot be empty or null.")
        }

        try {
            defaultApplication.resetPassword("someToken", "")
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "newPassword cannot be empty or null.")
        }

        try {
            defaultApplication.resetPassword("someToken", null)
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "newPassword cannot be empty or null.")
        }

    }

    //@since 1.0.0
    @Test
    void testGetAccountGithub() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                          tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                          accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                          groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                          passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"]]

        def internalDataStore = createStrictMock(InternalDataStore)
        def providerAccountResult = createStrictMock(ProviderAccountResult)
        ProviderAccountRequest request = Providers.GITHUB.account().setAccessToken("CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD").build()

        def providerAccountAccess = new DefaultProviderAccountAccess<GithubProviderData>(internalDataStore);
        providerAccountAccess.setProviderData(request.getProviderData())

        expect(internalDataStore.create(eq(properties.accounts.href), (Resource) reportMatcher(new ProviderAccountAccessEquals(providerAccountAccess)), (Class)eq(ProviderAccountResult))).andReturn(providerAccountResult)

        replay(internalDataStore, providerAccountResult)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)
        ProviderAccountResult accountResult = defaultApplication.getAccount(request)
        assertNotNull(accountResult)

        verify(internalDataStore, providerAccountResult)
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testSendVerificationEmailToken() {

        def accountStoreHref = "https://api.stormpath.com/v1/directories/6i2DiJWcsG6ZyUA8r0EwQU"
        def internalDataStore = createStrictMock(InternalDataStore)
        def defaultApplication = new DefaultApplication(internalDataStore, properties)
        VerificationEmailRequest verificationEmailRequest = createStrictMock(DefaultVerificationEmailRequest)
        def accountStore = createStrictMock(AccountStore)

        expect(verificationEmailRequest.getLogin()).andReturn("fooUsername")
        expect(verificationEmailRequest.getAccountStore()).andReturn(accountStore)
        expect(accountStore.getHref()).andReturn(accountStoreHref)
        expect(internalDataStore.create(defaultApplication.getHref() + "/verificationEmails", verificationEmailRequest, DefaultVerificationEmailRequest.class)).andReturn(null)

        replay internalDataStore, verificationEmailRequest, accountStore

        defaultApplication.sendVerificationEmail(verificationEmailRequest)

        verify internalDataStore, verificationEmailRequest, accountStore
    }

    /**
     * @since 1.0.0
     */
    @Test
    void testSendVerificationEmailTokenInvalidData() {

        def defaultApplication = new DefaultApplication(null)

        def verificationEmailRequest = new DefaultVerificationEmailRequest(null)

        try {
            defaultApplication.sendVerificationEmail(verificationEmailRequest)
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "verificationEmailRequest's email property is required.")
        }

        verificationEmailRequest.setLogin(null)
        try {
            defaultApplication.sendVerificationEmail(verificationEmailRequest)
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "verificationEmailRequest's email property is required.")
        }

        def dir = new DefaultDirectory(null)
        verificationEmailRequest.setLogin("fooUsername")
        verificationEmailRequest.setAccountStore(dir)
        try {
            defaultApplication.sendVerificationEmail(verificationEmailRequest)
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "verificationEmailRequest's accountStore has been specified but its href is null.")
        }
    }

    //@since 1.0.beta
    static class ProviderAccountAccessEquals implements IArgumentMatcher {

        private ProviderAccountAccess expected

        ProviderAccountAccessEquals(ProviderAccountAccess providerAccountAccess) {
            expected = providerAccountAccess;

        }
        boolean matches(Object o) {
            if (o == null || ! ProviderAccountAccess.isInstance(o)) {
                return false;
            }
            ProviderAccountAccess actual = (ProviderAccountAccess) o
            return (Objects.nullSafeEquals(expected.providerData, actual.providerData))
        }

        void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append("providerData: " + expected.providerData.toString())
        }
    }

    /**
     * @since 1.0.RC3
     */
    @Test
    void testAddAccountStoreNull() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def application = new DefaultApplication(internalDataStore, null)

        try {
            application.addAccountStore((String) null)
            fail("Should have thrown because of null 'hrefOrName'")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "hrefOrName cannot be null or empty.")
        }

        try {
            application.addAccountStore((DirectoryCriteria) null)
            fail("Should have thrown because of null 'DirectoryCriteria'")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "criteria cannot be null.")
        }

        try {
            application.addAccountStore((GroupCriteria) null)
            fail("Should have thrown because of null 'GroupCriteria'")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "criteria cannot be null.")
        }
    }

    /** @since 1.0.RC9 */
    @Test
    void testAddAccountStoreByHrefDirectories() {

        testAddAccountStoreByHref("directories", Directory)
    }

    /** @since 1.0.RC9 */
    @Test
    void testAddAccountStoreByHrefGroups() {

        testAddAccountStoreByHref("groups", Group)
    }

    /** @since 1.0.RC9 */
    @Test
    void testAddAccountStoreByHrefOrganizations() {

        testAddAccountStoreByHref("organizations", Organization)
    }

    /** @since 1.0.RC9 */
    private void testAddAccountStoreByHref(String accountStoreType, Class accountStoreClass) {

        def hrefprefix = "https://api.stormpath.com/v1"
        def hrefSuffix = "3KYfZ61QQXLR5ph34KXjpt"

        def internalDataStore = createStrictMock(InternalDataStore)
        def accountStore = createStrictMock(AccountStore)
        def accountStoreMapping = createStrictMock(ApplicationAccountStoreMapping)
        def application = new DefaultApplication(internalDataStore, ["href":hrefprefix+"/applications/"+hrefSuffix])

        def resourceUrl = hrefprefix + "/" + accountStoreType + "/" + hrefSuffix

        expect(internalDataStore.getResource(resourceUrl, accountStoreClass)).andReturn(accountStore)

        expect(internalDataStore.instantiate(ApplicationAccountStoreMapping)).andReturn(accountStoreMapping)
        expect(accountStoreMapping.setAccountStore(accountStore)).andReturn(accountStoreMapping);
        expect(accountStoreMapping.setApplication(application)).andReturn(accountStoreMapping);
        expect(accountStoreMapping.setListIndex(Integer.MAX_VALUE)).andReturn(accountStoreMapping);
        expect(internalDataStore.create("/accountStoreMappings", accountStoreMapping)).andReturn(accountStoreMapping)

        replay internalDataStore, accountStore, accountStoreMapping

        application.addAccountStore(resourceUrl)

        verify internalDataStore, accountStore, accountStoreMapping
    }

    /**
     * Asserts that https://github.com/stormpath/stormpath-sdk-java/issues/132 has been fixed
     * @since 1.0.RC4 */
    @Test
    void testPasswordResetToken() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                          tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                          accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                          groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                          passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"],
                          defaultAccountStoreMapping: [href: "https://api.stormpath.com/v1/accountStoreMappings/5dc0HbVMB8g3GWpSkOzqfF"],
                          defaultGroupStoreMapping: [href: "https://api.stormpath.com/v1/accountStoreMappings/5dc0HbVMB8g3GWpSkOzqfF"],
                          accountStoreMappings: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accountStoreMappings"],
                          customData: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/customData"]
        ]
        def returnedProperties = [ href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens/eyJraWQiOiI0Y0ZPSXNsZ3ZLTHNiakFFSWlXVjZaIiwiYWxnIjoiSFMyNTYifQ.eyJleHAiOjE0MjQyNzI3NjAsImp0aSI6IjJoVnlWdmhLRktyYWhBMVlabVVTUkUifQ.WQdIUrvE6Vtv6mGTcKsvG1ndQkv4Bza1ekWX9Y0LVt4",
                                   email : "test@stormpath.com",
                                   account: [ href : "https://api.stormpath.com/v1/accounts/1dEw3gHFhzyw8jmYFqlIld"]
        ]

        def apiKey = ApiKeys.builder().setId('foo').setSecret('bar').build()
        def apiKeyCredentials = new ApiKeyCredentials(apiKey)
        def cacheManager = new DefaultCacheManager()
        def requestExecutor = createStrictMock(RequestExecutor)
        def response = createStrictMock(Response)
        def mapMarshaller = new JacksonMapMarshaller();
        InputStream is = new ByteArrayInputStream(mapMarshaller.marshal(returnedProperties).getBytes());

        expect(requestExecutor.executeRequest((DefaultRequest) reportMatcher(new RequestMatcher(new DefaultRequest(HttpMethod.POST, "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"))))).andReturn(response)
        expect(response.isError()).andReturn(false)
        expect(response.hasBody()).andReturn(true)
        expect(response.getBody()).andReturn(is)
        expect(response.getHttpStatus()).andReturn(200)

        replay requestExecutor, response

        def dataStore = new DefaultDataStore(requestExecutor, "https://api.stormpath.com/v1", apiKeyCredentials, cacheManager)

        def application = new DefaultApplication(dataStore, properties)
        //Since this issue shows up only when the caching is enabled, let's make sure that it is indeed enabled, otherwise
        //we are not actually asserting that the issue has been fixed
        assertTrue(dataStore.isCachingEnabled())
        application.sendPasswordResetEmail("test@stormpath.com");

        //assert there is no cached representation:
        assertTrue dataStore.cacheResolver.getCache(PasswordResetToken).map.isEmpty()

        verify requestExecutor, response
    }

    //@since 1.0.RC
    static class ApplicationMatcher implements IArgumentMatcher {

        private Application expected

        ApplicationMatcher(Application application) {
            expected = application;

        }
        boolean matches(Object o) {
            if (o == null || ! Application.isInstance(o)) {
                return false;
            }
            Application actual = (Application) o
            return expected.toString().equals(actual.toString())
        }

        void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(expected.toString())
        }
    }

    //@since 1.0.RC4
    static class RequestMatcher implements IArgumentMatcher {

        private Request expected

        RequestMatcher(Request request) {
            expected = request;

        }
        boolean matches(Object o) {
            if (o == null || ! Request.isInstance(o)) {
                return false;
            }
            Request actual = (Request) o
            return expected.getMethod().equals(actual.getMethod()) && expected.getResourceUrl().equals(actual.getResourceUrl()) && expected.getQueryString().equals(actual.getQueryString())
        }

        void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(expected.toString())
        }
    }

}