/*
 * Copyright 2013 Stormpath, Inc. and contributors.
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
package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.AccountStatus
import com.stormpath.sdk.account.EmailVerificationToken
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupCriteria
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.group.GroupMembershipList
import com.stormpath.sdk.impl.directory.DefaultDirectory
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.group.DefaultGroupList
import com.stormpath.sdk.impl.group.DefaultGroupMembership
import com.stormpath.sdk.impl.group.DefaultGroupMembershipList
import com.stormpath.sdk.impl.resource.CollectionReference
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StatusProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.impl.tenant.DefaultTenant
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @author ecrisostomo
 * @since 0.8
 */
class DefaultAccountTest {

    @Test
    void testGetPropertyDescriptors() {

        def defaultAccount = new DefaultAccount(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultAccount.getPropertyDescriptors()

        assertEquals(13, propertyDescriptors.size())

        assertTrue(propertyDescriptors.get("username") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("email") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("surname") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("middleName") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("givenName") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("password") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("fullName") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("status") instanceof StatusProperty && propertyDescriptors.get("status").getType().equals(AccountStatus))
        assertTrue(propertyDescriptors.get("emailVerificationToken") instanceof ResourceReference && propertyDescriptors.get("emailVerificationToken").getType().equals(EmailVerificationToken))
        assertTrue(propertyDescriptors.get("directory") instanceof ResourceReference && propertyDescriptors.get("directory").getType().equals(Directory))
        assertTrue(propertyDescriptors.get("tenant") instanceof ResourceReference && propertyDescriptors.get("tenant").getType().equals(Tenant))
        assertTrue(propertyDescriptors.get("groups") instanceof CollectionReference && propertyDescriptors.get("groups").getType().equals(GroupList))
        assertTrue(propertyDescriptors.get("groupMemberships") instanceof CollectionReference && propertyDescriptors.get("groupMemberships").getType().equals(GroupMembershipList))
    }

    @Test
    void testMethods() {

        def properties = new HashMap<String, Object>();
        properties.put("href", "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf")
        properties.put("fullName", "Mel Ben Smuk")
        properties.put("emailVerificationToken", [href: "https://api.stormpath.com/v1/accounts/emailVerificationTokens/4VQxTP5I7Xio03QJTOwQy1"])
        properties.put("directory", [href: "https://api.stormpath.com/v1/directories/fwerh23948ru2euweouh"])
        properties.put("tenant", [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"])
        properties.put("groups", [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groups"])
        properties.put("groupMemberships", [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groupMemberships"])

        def internalDataStore = createStrictMock(InternalDataStore)
        def defaultAccount = new DefaultAccount(internalDataStore, properties)

        def innerProperties = [href: "https://api.stormpath.com/v1/accounts/emailVerificationTokens/4VQxTP5I7Xio03QJTOwQy1"]
        expect(internalDataStore.instantiate(EmailVerificationToken, innerProperties)).
                andReturn(new DefaultEmailVerificationToken(internalDataStore, innerProperties))

        innerProperties = [href: "https://api.stormpath.com/v1/directories/fwerh23948ru2euweouh"]
        expect(internalDataStore.instantiate(Directory, innerProperties)).andReturn(new DefaultDirectory(internalDataStore, innerProperties))

        innerProperties = [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"]
        expect(internalDataStore.instantiate(Tenant, innerProperties)).andReturn(new DefaultTenant(internalDataStore, innerProperties))

        innerProperties = [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groups"]
        expect(internalDataStore.instantiate(GroupList, innerProperties)).andReturn(new DefaultGroupList(internalDataStore, innerProperties))

        def groupCriteria = createStrictMock(GroupCriteria)
        expect(internalDataStore.getResource(innerProperties.href, GroupList, groupCriteria)).andReturn(new DefaultGroupList(internalDataStore, innerProperties))

        def groupCriteriaMap = [givenName: "some+search"]
        expect(internalDataStore.getResource(innerProperties.href, GroupList, groupCriteriaMap)).andReturn(new DefaultGroupList(internalDataStore, innerProperties))

        innerProperties = [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groupMemberships"]
        expect(internalDataStore.instantiate(GroupMembershipList, innerProperties)).andReturn(new DefaultGroupMembershipList(internalDataStore, innerProperties))

        expect(internalDataStore.delete(defaultAccount))

        def groupMembership =  new DefaultGroupMembership(internalDataStore)
        def group = createStrictMock(Group)
        expect(group.getHref()).andReturn("https://api.stormpath.com/v1/groups/werjower0283uroejfo").times(2)
        expect(internalDataStore.instantiate(isA(Class), isA(Map))).andReturn(groupMembership)
        expect(internalDataStore.create("/groupMemberships", groupMembership)).andReturn(groupMembership)

        replay internalDataStore, groupCriteria, group

        assertFalse(defaultAccount.isPrintableProperty("password"))

        assertNull(defaultAccount.getStatus())

        defaultAccount.setUsername("pacoman")
        defaultAccount.setEmail("some@email.com")
        defaultAccount.setSurname("Smuk")
        defaultAccount.setMiddleName("Ben")
        defaultAccount.setGivenName("Mel")
        defaultAccount.setStatus(AccountStatus.DISABLED)
        defaultAccount.setPassword("superPass0rd")

        assertEquals("pacoman", defaultAccount.getUsername())
        assertEquals("some@email.com", defaultAccount.getEmail())
        assertEquals("Smuk", defaultAccount.getSurname())
        assertEquals("Ben", defaultAccount.getMiddleName())
        assertEquals("Mel", defaultAccount.getGivenName())
        assertEquals(AccountStatus.DISABLED, defaultAccount.getStatus())
        assertEquals("Mel Ben Smuk", defaultAccount.getFullName())

        def resource = defaultAccount.getEmailVerificationToken()
        assertTrue(resource instanceof DefaultEmailVerificationToken && resource.getHref().endsWith("emailVerificationTokens/4VQxTP5I7Xio03QJTOwQy1"))

        resource = defaultAccount.getDirectory()
        assertTrue(resource instanceof DefaultDirectory && resource.getHref().endsWith("directories/fwerh23948ru2euweouh"))

        resource = defaultAccount.getTenant()
        assertTrue(resource instanceof DefaultTenant && resource.getHref().endsWith("tenants/jdhrgojeorigjj09etiij"))

        resource = defaultAccount.getGroups()
        assertTrue(resource instanceof DefaultGroupList && resource.getHref().endsWith("accounts/iouertnw48ufsjnsDFSf/groups"))

        resource = defaultAccount.getGroups(groupCriteria)
        assertTrue(resource instanceof DefaultGroupList && resource.getHref().endsWith("accounts/iouertnw48ufsjnsDFSf/groups"))

        resource = defaultAccount.getGroups(groupCriteriaMap)
        assertTrue(resource instanceof DefaultGroupList && resource.getHref().endsWith("accounts/iouertnw48ufsjnsDFSf/groups"))

        resource = defaultAccount.getGroupMemberships()
        assertTrue(resource instanceof DefaultGroupMembershipList && resource.getHref().endsWith("accounts/iouertnw48ufsjnsDFSf/groupMemberships"))

        defaultAccount.delete()

        defaultAccount.addGroup(group)

        verify internalDataStore, groupCriteria, group
    }
}
