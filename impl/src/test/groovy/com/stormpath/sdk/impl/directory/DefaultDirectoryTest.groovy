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
package com.stormpath.sdk.impl.directory

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountCriteria
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.directory.DirectoryStatus
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupCriteria
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.impl.account.DefaultAccountList
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.group.DefaultGroupList
import com.stormpath.sdk.impl.resource.CollectionReference
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StatusProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.impl.tenant.DefaultTenant
import com.stormpath.sdk.oauth.Provider
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.8
 */
class DefaultDirectoryTest {

    @Test
    void testGetPropertyDescriptors() {

        DefaultDirectory defaultDirectory = new DefaultDirectory(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultDirectory.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 7)

        assertTrue(propertyDescriptors.get("name") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("description") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("status") instanceof StatusProperty)
        assertTrue(propertyDescriptors.get("tenant") instanceof ResourceReference)
        assertTrue(propertyDescriptors.get("accounts") instanceof CollectionReference)
        assertTrue(propertyDescriptors.get("groups") instanceof CollectionReference)
        assertTrue(propertyDescriptors.get("provider") instanceof ResourceReference && propertyDescriptors.get("provider").getType().equals(Provider))

    }


    @Test
    void testMethods() {

        InternalDataStore internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf",
                name: "My Directory",
                description: "My Description",
                accounts: [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/accounts"],
                groups: [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/groups"],
                tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"]
        ]

        DefaultDirectory defaultDirectory = new DefaultDirectory(internalDataStore, properties)

        assertNull(defaultDirectory.getStatus())

        defaultDirectory.setName("My new Directory")
        defaultDirectory.setDescription("My new Description")
        defaultDirectory.setStatus(DirectoryStatus.DISABLED)

        assertEquals(defaultDirectory.getName(), "My new Directory")
        assertEquals(defaultDirectory.getDescription(), "My new Description")
        assertEquals(defaultDirectory.getStatus(), DirectoryStatus.DISABLED)

        expect(internalDataStore.instantiate(AccountList, properties.accounts)).
                andReturn(new DefaultAccountList(internalDataStore, properties.accounts))

        def accountCriteria = createStrictMock(AccountCriteria)
        expect(internalDataStore.getResource(properties.accounts.href, AccountList, accountCriteria)).
                andReturn(new DefaultAccountList(internalDataStore, properties.accounts))

        def accountCriteriaMap = [username: "some+search"]
        expect(internalDataStore.getResource(properties.accounts.href, AccountList, accountCriteriaMap)).
                andReturn(new DefaultAccountList(internalDataStore, properties.accounts))

        expect(internalDataStore.instantiate(GroupList, properties.groups)).
                andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        def groupCriteria = createStrictMock(GroupCriteria)
        expect(internalDataStore.getResource(properties.groups.href, GroupList, groupCriteria)).
                andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        def groupCriteriaMap = [name: "some+search"]
        expect(internalDataStore.getResource(properties.groups.href, GroupList, groupCriteriaMap)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        expect(internalDataStore.instantiate(Tenant, properties.tenant)).
                andReturn(new DefaultTenant(internalDataStore, properties.tenant))

        expect(internalDataStore.delete(defaultDirectory))

        def account = createStrictMock(Account)
        expect(internalDataStore.create("https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/accounts", account)).andReturn(account)

        expect(internalDataStore.create("https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/accounts?registrationWorkflowEnabled=true", account)).andReturn(account)

        expect(internalDataStore.create("https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/accounts?registrationWorkflowEnabled=false", account)).andReturn(account)

        def group = createStrictMock(Group)
        expect(internalDataStore.create("https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/groups", group)).andReturn(group)

        replay internalDataStore, accountCriteria, groupCriteria, account, group

        def resource = defaultDirectory.getAccounts()
        assertTrue(resource instanceof DefaultAccountList && resource.getHref().equals(properties.accounts.href))

        resource = defaultDirectory.getAccounts(accountCriteria)
        assertTrue(resource instanceof DefaultAccountList && resource.getHref().equals(properties.accounts.href))

        resource = defaultDirectory.getAccounts(accountCriteriaMap)
        assertTrue(resource instanceof DefaultAccountList && resource.getHref().equals(properties.accounts.href))

        resource = defaultDirectory.getGroups()
        assertTrue(resource instanceof DefaultGroupList && resource.getHref().equals(properties.groups.href))

        resource = defaultDirectory.getGroups(groupCriteria)
        assertTrue(resource instanceof DefaultGroupList && resource.getHref().equals(properties.groups.href))

        resource = defaultDirectory.getGroups(groupCriteriaMap)
        assertTrue(resource instanceof DefaultGroupList && resource.getHref().equals(properties.groups.href))

        resource = defaultDirectory.getTenant()
        assertTrue(resource instanceof DefaultTenant && resource.getHref().equals(properties.tenant.href))

        defaultDirectory.delete()

        defaultDirectory.createAccount(account)

        defaultDirectory.createAccount(account, true)
        defaultDirectory.createAccount(account, false)

        defaultDirectory.createGroup(group)

        verify internalDataStore, accountCriteria, groupCriteria, account, group
    }
}
