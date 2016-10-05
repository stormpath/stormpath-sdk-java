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
package com.stormpath.sdk.impl.group

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupMembership
import com.stormpath.sdk.group.GroupMembershipList
import com.stormpath.sdk.group.GroupStatus
import com.stormpath.sdk.impl.account.DefaultAccountList
import com.stormpath.sdk.impl.directory.DefaultDirectory
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.CollectionReference
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.EnumProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.impl.tenant.DefaultTenant
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.8
 */
class DefaultGroupTest {

    @Test
    void testGetPropertyDescriptors() {

        DefaultGroup defaultGroup = new DefaultGroup(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultGroup.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 8)

        assertTrue(propertyDescriptors.get("name") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("description") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("status") instanceof EnumProperty && propertyDescriptors.get("status").getType().equals(GroupStatus))
        assertTrue(propertyDescriptors.get("customData") instanceof ResourceReference && propertyDescriptors.get("customData").getType().equals(CustomData))
        assertTrue(propertyDescriptors.get("directory") instanceof ResourceReference && propertyDescriptors.get("directory").getType().equals(Directory))
        assertTrue(propertyDescriptors.get("tenant") instanceof ResourceReference && propertyDescriptors.get("tenant").getType().equals(Tenant))
        assertTrue(propertyDescriptors.get("accounts") instanceof CollectionReference && propertyDescriptors.get("accounts").getType().equals(AccountList))
        assertTrue(propertyDescriptors.get("accountMemberships") instanceof CollectionReference && propertyDescriptors.get("accountMemberships").getType().equals(GroupMembershipList))
    }

    @Test
    void testMethods() {

        def properties = [href: "https://api.stormpath.com/v1/groups/koaertnw47ufsjngDFSs",
                          name: "My group",
                          desctiption: "My group description",
                          directory: [href: "https://api.stormpath.com/v1/directories/fwerh23948ru2euweouh"],
                          tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"],
                          accounts: [href: "https://api.stormpath.com/v1/groups/koaertnw47ufsjngDFSs/accounts"],
                          accountMemberships: [href: "https://api.stormpath.com/v1/groups/koaertnw47ufsjngDFSs/accountMemberships"]]

        InternalDataStore internalDataStore = createStrictMock(InternalDataStore)
        Group defaultGroup = new DefaultGroup(internalDataStore, properties)

        assertNull(defaultGroup.getStatus())

        defaultGroup = defaultGroup.setName("My new group")
            .setDescription("My new description")
            .setStatus(GroupStatus.DISABLED)

        assertEquals(defaultGroup.getName(), "My new group")
        assertEquals(defaultGroup.getDescription(), "My new description")
        assertEquals(defaultGroup.getStatus(), GroupStatus.DISABLED)

        expect(internalDataStore.instantiate(Directory, properties.directory)).andReturn(new DefaultDirectory(internalDataStore, properties.directory))

        expect(internalDataStore.instantiate(Tenant, properties.tenant)).andReturn(new DefaultTenant(internalDataStore, properties.tenant))

        expect(internalDataStore.instantiate(AccountList, properties.accounts)).andReturn(new DefaultAccountList(internalDataStore, properties.accounts))

        def accountCriteriaMap = [name: "some+search"]
        expect(internalDataStore.instantiate(AccountList, properties.accounts)).andReturn(new DefaultAccountList(internalDataStore, properties.accounts))
        expect(internalDataStore.getResource(properties.accounts.href, AccountList, accountCriteriaMap)).andReturn(new DefaultAccountList(internalDataStore, properties.accounts))

        expect(internalDataStore.instantiate(GroupMembershipList, properties.accountMemberships)).andReturn(new DefaultGroupMembershipList(internalDataStore, properties.accountMemberships))

        expect(internalDataStore.delete(defaultGroup))

        def groupMembership =  new DefaultGroupMembership(internalDataStore)
        Account account = createStrictMock(Account)
        expect(account.getHref()).andReturn("https://api.stormpath.com/v1/accounts/abrqtwar0j23ur1ejfW").times(2)
        expect(internalDataStore.instantiate(eq(GroupMembership.class))).andReturn(groupMembership)
        expect(internalDataStore.create(eq("/groupMemberships"), same(groupMembership))).andReturn(groupMembership)

        replay internalDataStore, account

        def resource = defaultGroup.getDirectory()
        assertTrue(resource instanceof DefaultDirectory && resource.getHref().equals(properties.directory.href))

        resource = defaultGroup.getTenant()
        assertTrue(resource instanceof DefaultTenant && resource.getHref().equals(properties.tenant.href))

        resource = defaultGroup.getAccounts()
        assertTrue(resource instanceof DefaultAccountList && resource.getHref().equals(properties.accounts.href))

        resource = defaultGroup.getAccounts(accountCriteriaMap)
        assertTrue(resource instanceof DefaultAccountList && resource.getHref().equals(properties.accounts.href))

        resource = defaultGroup.getAccountMemberships()
        assertTrue(resource instanceof DefaultGroupMembershipList && resource.getHref().equals(properties.accountMemberships.href))

        defaultGroup.delete()

        defaultGroup.addAccount(account)

        verify internalDataStore, account
    }
}
