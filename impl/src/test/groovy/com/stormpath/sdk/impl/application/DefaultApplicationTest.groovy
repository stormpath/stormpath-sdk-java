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

import com.stormpath.sdk.account.*
import com.stormpath.sdk.application.AccountStoreMapping
import com.stormpath.sdk.application.AccountStoreMappingList
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationStatus
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.authc.UsernamePasswordRequest
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.group.*
import com.stormpath.sdk.impl.account.DefaultAccountList
import com.stormpath.sdk.impl.account.DefaultPasswordResetToken
import com.stormpath.sdk.impl.authc.BasicLoginAttempt
import com.stormpath.sdk.impl.authc.DefaultBasicLoginAttempt
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.group.DefaultGroupList
import com.stormpath.sdk.impl.provider.DefaultProviderAccountAccess
import com.stormpath.sdk.impl.provider.ProviderAccountAccess
import com.stormpath.sdk.impl.provider.ProviderAccountResultHelper
import com.stormpath.sdk.impl.resource.AbstractResource
import com.stormpath.sdk.impl.resource.CollectionReference
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StatusProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.impl.tenant.DefaultTenant
import com.stormpath.sdk.lang.Objects
import com.stormpath.sdk.provider.FacebookProviderData
import com.stormpath.sdk.provider.ProviderAccountRequest
import com.stormpath.sdk.provider.ProviderAccountResult
import com.stormpath.sdk.provider.Providers
import com.stormpath.sdk.resource.Resource
import com.stormpath.sdk.tenant.Tenant
import org.easymock.EasyMock
import org.easymock.IArgumentMatcher
import org.testng.annotations.Test

import java.lang.reflect.Field
import java.lang.reflect.Modifier

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.8
 */
class DefaultApplicationTest {

    @Test
    void testGetPropertyDescriptors() {

        def defaultApplication = new DefaultApplication(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultApplication.getPropertyDescriptors()

        assertEquals( propertyDescriptors.size(), 10)

        assertTrue(propertyDescriptors.get("name") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("description") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("status") instanceof StatusProperty && propertyDescriptors.get("status").getType().equals(ApplicationStatus))
        assertTrue(propertyDescriptors.get("tenant") instanceof ResourceReference && propertyDescriptors.get("tenant").getType().equals(Tenant))
        assertTrue(propertyDescriptors.get("defaultAccountStoreMapping") instanceof ResourceReference && propertyDescriptors.get("defaultAccountStoreMapping").getType().equals(AccountStoreMapping))
        assertTrue(propertyDescriptors.get("defaultGroupStoreMapping") instanceof ResourceReference && propertyDescriptors.get("defaultGroupStoreMapping").getType().equals(AccountStoreMapping))
        assertTrue(propertyDescriptors.get("accounts") instanceof CollectionReference && propertyDescriptors.get("accounts").getType().equals(AccountList))
        assertTrue(propertyDescriptors.get("groups") instanceof CollectionReference && propertyDescriptors.get("groups").getType().equals(GroupList))
        assertTrue(propertyDescriptors.get("passwordResetTokens") instanceof CollectionReference && propertyDescriptors.get("passwordResetTokens").getType().equals(PasswordResetTokenList))
        assertTrue(propertyDescriptors.get("accountStoreMappings") instanceof CollectionReference && propertyDescriptors.get("accountStoreMappings").getType().equals(AccountStoreMappingList))
    }

    @Test
    void testMethods() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"]]

        def internalDataStore = createStrictMock(InternalDataStore)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)

        assertNull(defaultApplication.getStatus())

        defaultApplication = defaultApplication.setStatus(ApplicationStatus.DISABLED)
            .setName("App Name")
            .setDescription("App Description")

        assertEquals(defaultApplication.getStatus(), ApplicationStatus.DISABLED)
        assertEquals(defaultApplication.getName(), "App Name")
        assertEquals(defaultApplication.getDescription(), "App Description")

        expect(internalDataStore.instantiate(GroupList, properties.groups)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        def groupCriteria = createStrictMock(GroupCriteria)
        expect(internalDataStore.getResource(properties.groups.href, GroupList, groupCriteria)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        def groupCriteriaMap = [name: "some+search"]
        expect(internalDataStore.getResource(properties.groups.href, GroupList, groupCriteriaMap)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        expect(internalDataStore.instantiate(AccountList, properties.accounts)).andReturn(new DefaultAccountList(internalDataStore, properties.accounts))

        def accountCriteria = createStrictMock(AccountCriteria)
        expect(internalDataStore.getResource(properties.accounts.href, AccountList, accountCriteria)).andReturn(new DefaultAccountList(internalDataStore, properties.accounts))

        def accountCriteriaMap = [surname: "some+search"]
        expect(internalDataStore.getResource(properties.accounts.href, AccountList, accountCriteriaMap)).andReturn(new DefaultAccountList(internalDataStore, properties.accounts))

        expect(internalDataStore.instantiate(Tenant, properties.tenant)).andReturn(new DefaultTenant(internalDataStore, properties.tenant))

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

        def authenticationResult = createStrictMock(AuthenticationResult)
        def defaultBasicLoginAttempt = new DefaultBasicLoginAttempt(internalDataStore)
        expect(internalDataStore.instantiate(BasicLoginAttempt)).andReturn(defaultBasicLoginAttempt)
        defaultBasicLoginAttempt.setType("basic")
        defaultBasicLoginAttempt.setValue("dXNlcm5hbWU6cGFzc3dvcmQ=")
        expect(internalDataStore.create(properties.href + "/loginAttempts", defaultBasicLoginAttempt, AuthenticationResult)).andReturn(authenticationResult)

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

        defaultApplication.delete()

        assertEquals(defaultApplication.sendPasswordResetEmail("some@email.com"), account)
        assertEquals(defaultApplication.verifyPasswordResetToken("token"), account)
        assertEquals(defaultApplication.authenticateAccount(new UsernamePasswordRequest("username", "password")), authenticationResult)

        verify internalDataStore, groupCriteria, accountCriteria, account
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

        Group group = null
        app.createGroup(group)
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
                accountStoreMappings: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accountStoreMappings"]
        ]

        def accountStoreHref = "https://api.stormpath.com/v1/directories/6i2DiJWcsG6ZyUA8r0EwQU"
        def groupHref = "https://api.stormpath.com/v1/groups/6dWPIXEi4MvGTnQcWApizf"

        def dataStore = createStrictMock(InternalDataStore)
        def accountStore = createStrictMock(AccountStore)
        def group = createStrictMock(Group)
        def accountStoreMappings = createStrictMock(AccountStoreMappingList)
        def iterator = createStrictMock(Iterator)
        def accountStoreMapping = createStrictMock(AccountStoreMapping)
        def newAccountStoreMapping = createStrictMock(AccountStoreMapping)

        expect(dataStore.instantiate(AccountStoreMappingList, properties.accountStoreMappings)).andReturn(accountStoreMappings)
        expect(accountStoreMappings.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountStoreMapping)
        expect(accountStoreMapping.getAccountStore()).andReturn(accountStore)
        expect(accountStore.getHref()).andReturn(accountStoreHref)
        expect(group.getHref()).andReturn(groupHref)
        expect(iterator.hasNext()).andReturn(false)
        expect(dataStore.instantiate(AccountStoreMapping)).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.setAccountStore(group)).andReturn(newAccountStoreMapping)

        def newPropertiesState = new LinkedHashMap<String, Object>()
        newPropertiesState.putAll(properties)
        def modifiedApp = new DefaultApplication(null, newPropertiesState)
        setNewValue(AbstractResource, modifiedApp, "dirtyProperties", [accountStoreMappings: accountStoreMappings])

        expect(newAccountStoreMapping.setApplication((Application) reportMatcher(new ApplicationMatcher(modifiedApp)))).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.setListIndex(Integer.MAX_VALUE)).andReturn(newAccountStoreMapping)
        expect(dataStore.create("/accountStoreMappings", newAccountStoreMapping)).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.setDefaultAccountStore(true)).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.save())

        modifiedApp = new DefaultApplication(null, newPropertiesState)
        setNewValue(AbstractResource, modifiedApp, "dirtyProperties", [defaultAccountStoreMapping: newAccountStoreMapping])

        expect(dataStore.save((Application) reportMatcher(new ApplicationMatcher(modifiedApp))))

        //Second execution
        expect(accountStoreMappings.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountStoreMapping)
        expect(accountStoreMapping.getAccountStore()).andReturn(accountStore)
        expect(accountStore.getHref()).andReturn(accountStoreHref) times 2
        expect(accountStoreMapping.setDefaultAccountStore(true)).andReturn(accountStoreMapping)
        expect(accountStoreMapping.save())

        modifiedApp = new DefaultApplication(null, newPropertiesState)
        setNewValue(AbstractResource, modifiedApp, "dirtyProperties", [defaultAccountStoreMapping: accountStoreMapping])

        expect(dataStore.save((Application) reportMatcher(new ApplicationMatcher(modifiedApp))))

        replay dataStore, accountStore, group, accountStoreMappings, iterator, accountStoreMapping, newAccountStoreMapping

        def app = new DefaultApplication(dataStore, properties)
        app.setDefaultAccountStore(group)
        app.setDefaultAccountStore(accountStore)

        verify dataStore, accountStore, group, accountStoreMappings, iterator, accountStoreMapping, newAccountStoreMapping
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
                accountStoreMappings: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accountStoreMappings"]
        ]

        def accountStoreHref = "https://api.stormpath.com/v1/directories/6i2DiJWcsG6ZyUA8r0EwQU"
        def groupHref = "https://api.stormpath.com/v1/groups/6dWPIXEi4MvGTnQcWApizf"

        def dataStore = createStrictMock(InternalDataStore)
        def accountStore = createStrictMock(AccountStore)
        def group = createStrictMock(Group)
        def accountStoreMappings = createStrictMock(AccountStoreMappingList)
        def iterator = createStrictMock(Iterator)
        def accountStoreMapping = createStrictMock(AccountStoreMapping)
        def newAccountStoreMapping = createStrictMock(AccountStoreMapping)

        expect(dataStore.instantiate(AccountStoreMappingList, properties.accountStoreMappings)).andReturn(accountStoreMappings)
        expect(accountStoreMappings.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountStoreMapping)
        expect(accountStoreMapping.getAccountStore()).andReturn(accountStore)
        expect(accountStore.getHref()).andReturn(accountStoreHref)
        expect(group.getHref()).andReturn(groupHref)
        expect(iterator.hasNext()).andReturn(false)
        expect(dataStore.instantiate(AccountStoreMapping)).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.setAccountStore(group)).andReturn(newAccountStoreMapping)

        def newPropertiesState = new LinkedHashMap<String, Object>()
        newPropertiesState.putAll(properties)
        def modifiedApp = new DefaultApplication(null, newPropertiesState)
        setNewValue(AbstractResource, modifiedApp, "dirtyProperties", [accountStoreMappings: accountStoreMappings])

        expect(newAccountStoreMapping.setApplication((Application) reportMatcher(new ApplicationMatcher(modifiedApp)))).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.setListIndex(Integer.MAX_VALUE)).andReturn(newAccountStoreMapping)
        expect(dataStore.create("/accountStoreMappings", newAccountStoreMapping)).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.setDefaultGroupStore(true)).andReturn(newAccountStoreMapping)
        expect(newAccountStoreMapping.save())

        modifiedApp = new DefaultApplication(null, newPropertiesState)
        setNewValue(AbstractResource, modifiedApp, "dirtyProperties", [defaultGroupStoreMapping: newAccountStoreMapping])

        expect(dataStore.save((Application) reportMatcher(new ApplicationMatcher(modifiedApp))))

        //Second execution
        expect(accountStoreMappings.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountStoreMapping)
        expect(accountStoreMapping.getAccountStore()).andReturn(accountStore)
        expect(accountStore.getHref()).andReturn(accountStoreHref) times 2
        expect(accountStoreMapping.setDefaultGroupStore(true)).andReturn(accountStoreMapping)
        expect(accountStoreMapping.save())

        modifiedApp = new DefaultApplication(null, newPropertiesState)
        setNewValue(AbstractResource, modifiedApp, "dirtyProperties", [defaultGroupStoreMapping: accountStoreMapping])

        expect(dataStore.save((Application) reportMatcher(new ApplicationMatcher(modifiedApp))))

        replay dataStore, accountStore, group, accountStoreMappings, iterator, accountStoreMapping, newAccountStoreMapping

        def app = new DefaultApplication(dataStore, properties)
        app.setDefaultGroupStore(group)
        app.setDefaultGroupStore(accountStore)

        verify dataStore, accountStore, group, accountStoreMappings, iterator, accountStoreMapping, newAccountStoreMapping
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
        def providerAccountResultHelper = createStrictMock(ProviderAccountResultHelper)
        def providerAccountResult = createStrictMock(ProviderAccountResult)
        ProviderAccountRequest request = Providers.FACEBOOK.account().setAccessToken("CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD").build()

        def providerAccountAccess = new DefaultProviderAccountAccess<FacebookProviderData>(internalDataStore);
        providerAccountAccess.setProviderData(request.getProviderData())

        expect(internalDataStore.create(eq(properties.accounts.href), (Resource) reportMatcher(new ProviderAccountAccessEquals(providerAccountAccess)), (Class)eq(ProviderAccountResultHelper))).andReturn(providerAccountResultHelper)
        expect(providerAccountResultHelper.getProviderAccountResult()).andReturn(providerAccountResult)

        replay(internalDataStore, providerAccountResultHelper, providerAccountResult)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)
        ProviderAccountResult accountResult = defaultApplication.getAccount(request)
        assertNotNull(accountResult)

        verify(internalDataStore, providerAccountResultHelper, providerAccountResult)
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

        assertEquals(defaultApplication.sendPasswordResetEmail("some@email.com"), account)
        assertEquals(defaultApplication.resetPassword("token", "myNewPassword"), account)
        assertEquals(defaultApplication.authenticateAccount(new UsernamePasswordRequest("username", "myNewPassword")), authenticationResult)

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

    //@since 1.0.RC
    private void setNewValue(Class clazz, Object object, String fieldName, Object value){
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        field.set(object, value)
    }

}