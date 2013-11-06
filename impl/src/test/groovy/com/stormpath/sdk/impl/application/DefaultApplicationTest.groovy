/*
 * Copyright 2013 Stormpath, Inc.
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

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountCriteria
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.account.PasswordResetToken
import com.stormpath.sdk.application.ApplicationStatus
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.authc.UsernamePasswordRequest
import com.stormpath.sdk.group.CreateGroupRequest
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupCriteria
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.impl.account.DefaultAccountList
import com.stormpath.sdk.impl.account.DefaultPasswordResetToken
import com.stormpath.sdk.impl.authc.BasicLoginAttempt
import com.stormpath.sdk.impl.authc.DefaultBasicLoginAttempt
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.group.DefaultCreateGroupRequest
import com.stormpath.sdk.impl.group.DefaultGroupList
import com.stormpath.sdk.impl.resource.CollectionReference
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StatusProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.impl.tenant.DefaultTenant
import com.stormpath.sdk.tenant.Tenant
import org.easymock.EasyMock
import org.testng.annotations.Test

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

        assertEquals( propertyDescriptors.size(), 7)

        assertTrue(propertyDescriptors.get("name") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("description") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("status") instanceof StatusProperty && propertyDescriptors.get("status").getType().equals(ApplicationStatus))
        assertTrue(propertyDescriptors.get("tenant") instanceof ResourceReference && propertyDescriptors.get("tenant").getType().equals(Tenant))
        assertTrue(propertyDescriptors.get("accounts") instanceof CollectionReference && propertyDescriptors.get("accounts").getType().equals(AccountList))
        assertTrue(propertyDescriptors.get("groups") instanceof CollectionReference && propertyDescriptors.get("groups").getType().equals(GroupList))
        assertTrue(propertyDescriptors.get("passwordResetTokens") instanceof CollectionReference && propertyDescriptors.get("passwordResetTokens").getType().equals(PasswordResetTokenList))
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

        defaultApplication.setStatus(ApplicationStatus.DISABLED)
        defaultApplication.setName("App Name")
        defaultApplication.setDescription("App Description")

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

        expect(internalDataStore.instantiate(PasswordResetToken, [href: properties.passwordResetTokens.href + "/token"]))andReturn(new DefaultPasswordResetToken(internalDataStore, innerProperties))
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
    void testCreateGroupWithRequest() {

        def properties = [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
                accounts: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"],
                groups: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"],
                passwordResetTokens: [href: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/passwordResetTokens"]]

        def internalDataStore = createStrictMock(InternalDataStore)
        def request = createStrictMock(DefaultCreateGroupRequest)
        def group = createStrictMock(Group)
        def groupList = createStrictMock(GroupList)
        def returnedGroup = createStrictMock(Group)

        def defaultApplication = new DefaultApplication(internalDataStore, properties)

        expect(request.getGroup()).andReturn(group)
        expect(internalDataStore.instantiate(GroupList, [href:"https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups"])).andReturn(groupList)
        expect(groupList.getHref()).andReturn("https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups")
        expect(internalDataStore.create("https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/groups", group)).andReturn(returnedGroup)

        replay internalDataStore, request, group, groupList, returnedGroup

        assertEquals(defaultApplication.createGroup(request), returnedGroup)

        verify internalDataStore, request, group, groupList, returnedGroup
    }

    @Test
    void testCreateGroupWithGroup() {

        def group = createStrictMock(Group)
        def partiallyMockedDefaultApplication = createMockBuilder(DefaultApplication.class)
                .addMockedMethod("createGroup", CreateGroupRequest).createMock();

        expect(partiallyMockedDefaultApplication.createGroup((CreateGroupRequest)EasyMock.anyObject())).andReturn(group)

        replay partiallyMockedDefaultApplication, group

        assertEquals(partiallyMockedDefaultApplication.createGroup(group), group)

        verify partiallyMockedDefaultApplication, group
    }

}