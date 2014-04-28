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
package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.AccountStatus
import com.stormpath.sdk.account.EmailVerificationToken
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.*
import com.stormpath.sdk.impl.directory.DefaultDirectory
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.group.DefaultGroupList
import com.stormpath.sdk.impl.group.DefaultGroupMembership
import com.stormpath.sdk.impl.group.DefaultGroupMembershipList
import com.stormpath.sdk.impl.provider.DefaultProviderData
import com.stormpath.sdk.impl.provider.IdentityProviderType
import com.stormpath.sdk.impl.resource.CollectionReference
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StatusProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.impl.tenant.DefaultTenant
import com.stormpath.sdk.provider.ProviderData
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.8
 */
class DefaultAccountTest {

    @Test
    void testGetPropertyDescriptors() {

        def defaultAccount = new DefaultAccount(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultAccount.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 15)

        assertTrue(propertyDescriptors.get("username") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("email") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("surname") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("middleName") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("givenName") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("password") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("fullName") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("status") instanceof StatusProperty && propertyDescriptors.get("status").getType().equals(AccountStatus))
        assertTrue(propertyDescriptors.get("emailVerificationToken") instanceof ResourceReference && propertyDescriptors.get("emailVerificationToken").getType().equals(EmailVerificationToken))
        assertTrue(propertyDescriptors.get("customData") instanceof ResourceReference && propertyDescriptors.get("customData").getType().equals(CustomData))
        assertTrue(propertyDescriptors.get("directory") instanceof ResourceReference && propertyDescriptors.get("directory").getType().equals(Directory))
        assertTrue(propertyDescriptors.get("tenant") instanceof ResourceReference && propertyDescriptors.get("tenant").getType().equals(Tenant))
        assertTrue(propertyDescriptors.get("groups") instanceof CollectionReference && propertyDescriptors.get("groups").getType().equals(GroupList))
        assertTrue(propertyDescriptors.get("groupMemberships") instanceof CollectionReference && propertyDescriptors.get("groupMemberships").getType().equals(GroupMembershipList))
        assertTrue(propertyDescriptors.get("providerData") instanceof ResourceReference && propertyDescriptors.get("providerData").getType().equals(ProviderData))
    }

    @Test
    void testMethods() {

        def properties = [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf",
                          fullName: "Mel Ben Smuk",
                          emailVerificationToken: [href: "https://api.stormpath.com/v1/accounts/emailVerificationTokens/4VQxTP5I7Xio03QJTOwQy1"],
                          directory: [href: "https://api.stormpath.com/v1/directories/fwerh23948ru2euweouh"],
                          tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"],
                          groups: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groups"],
                          groupMemberships: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groupMemberships"],
                          providerData: [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData"]]

        def internalDataStore = createStrictMock(InternalDataStore)
        def defaultAccount = new DefaultAccount(internalDataStore, properties)

        assertFalse(defaultAccount.isPrintableProperty("password"))
        assertNull(defaultAccount.getStatus())

        defaultAccount.setUsername("pacoman")
        defaultAccount.setEmail("some@email.com")
        defaultAccount.setSurname("Smuk")
        defaultAccount.setMiddleName("Ben")
        defaultAccount.setGivenName("Mel")
        defaultAccount.setStatus(AccountStatus.DISABLED)
        defaultAccount.setPassword("superPass0rd")

        assertEquals(defaultAccount.getUsername(), "pacoman")
        assertEquals(defaultAccount.getEmail(), "some@email.com")
        assertEquals(defaultAccount.getSurname(), "Smuk")
        assertEquals(defaultAccount.getMiddleName(), "Ben")
        assertEquals(defaultAccount.getGivenName(), "Mel")
        assertEquals(defaultAccount.getStatus(), AccountStatus.DISABLED)
        assertEquals(defaultAccount.getFullName(), "Mel Ben Smuk")

        expect(internalDataStore.instantiate(EmailVerificationToken, properties.emailVerificationToken)).
                andReturn(new DefaultEmailVerificationToken(internalDataStore, properties.emailVerificationToken))

        expect(internalDataStore.instantiate(Directory, properties.directory)).andReturn(new DefaultDirectory(internalDataStore, properties.directory))

        expect(internalDataStore.instantiate(Tenant, properties.tenant)).andReturn(new DefaultTenant(internalDataStore, properties.tenant))

        expect(internalDataStore.instantiate(GroupList, properties.groups)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        def groupCriteria = createStrictMock(GroupCriteria)
        expect(internalDataStore.getResource(properties.groups.href, GroupList, groupCriteria)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        def groupCriteriaMap = [name: "some+search"]
        expect(internalDataStore.getResource(properties.groups.href, GroupList, groupCriteriaMap)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        expect(internalDataStore.instantiate(GroupMembershipList, properties.groupMemberships)).andReturn(new DefaultGroupMembershipList(internalDataStore, properties.groupMemberships))

        expect(internalDataStore.getResource(properties.providerData.href, ProviderData.class, "providerId", IdentityProviderType.IDENTITY_PROVIDERDATA_CLASS_MAP))
                .andReturn(new DefaultProviderData(internalDataStore, properties.providerData))

        expect(internalDataStore.delete(defaultAccount))

        def groupMembership =  new DefaultGroupMembership(internalDataStore)
        def group = createStrictMock(Group)
        expect(group.getHref()).andReturn("https://api.stormpath.com/v1/groups/werjower0283uroejfo").times(2)
        expect(internalDataStore.instantiate(eq(GroupMembership.class))).andReturn(groupMembership)
        expect(internalDataStore.create(eq("/groupMemberships"), same(groupMembership))).andReturn(groupMembership)

        replay internalDataStore, groupCriteria, group

        def resource = defaultAccount.getEmailVerificationToken()
        assertTrue(resource instanceof DefaultEmailVerificationToken && resource.getHref().equals(properties.emailVerificationToken.href))

        resource = defaultAccount.getDirectory()
        assertTrue(resource instanceof DefaultDirectory && resource.getHref().equals(properties.directory.href))

        resource = defaultAccount.getTenant()
        assertTrue(resource instanceof DefaultTenant && resource.getHref().equals(properties.tenant.href))

        resource = defaultAccount.getGroups()
        assertTrue(resource instanceof DefaultGroupList && resource.getHref().equals(properties.groups.href))

        resource = defaultAccount.getGroups(groupCriteria)
        assertTrue(resource instanceof DefaultGroupList && resource.getHref().equals(properties.groups.href))

        resource = defaultAccount.getGroups(groupCriteriaMap)
        assertTrue(resource instanceof DefaultGroupList && resource.getHref().equals(properties.groups.href))

        resource = defaultAccount.getGroupMemberships()
        assertTrue(resource instanceof DefaultGroupMembershipList && resource.getHref().equals(properties.groupMemberships.href))

        resource = defaultAccount.getProviderData()
        assertTrue(resource instanceof DefaultProviderData && resource.getHref().equals(properties.providerData.href))
        resource = defaultAccount.getProviderData() //Second invocation must not internally call internalDataStore.getResource(...) as it is already fully available in the internal properties
        assertTrue(resource instanceof DefaultProviderData && resource.getHref().equals(properties.providerData.href))

        defaultAccount.delete()

        defaultAccount.addGroup(group)

        verify internalDataStore, groupCriteria, group
    }

    @Test
    void testIsMemberOfGroup() {

        def groupName = "fooName"
        def groupHref = "https://api.stormpath.com/v1/groups/7frJxiVEfZB9NaXw5vLvCA"
        def groupValues = [href: groupHref, name: groupName]

        def properties = [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf",
                groups: [
                        href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/groups",
                        items: [
                                groupValues
                        ],
                        limit: 25,
                        offset: 0
                ],
        ]


        def internalDataStore = createStrictMock(InternalDataStore)
        def defaultAccount = new DefaultAccount(internalDataStore, properties)
        def mockGroup = createStrictMock(Group)

        expect(internalDataStore.instantiate(GroupList, properties.groups)).andReturn(new DefaultGroupList(internalDataStore, properties.groups))

        expect(internalDataStore.instantiate(Group, groupValues)).andReturn(mockGroup)
        expect(mockGroup.getName()).andReturn(properties.groups.items.get(0).name)
        expect(mockGroup.getHref()).andReturn(properties.groups.items.get(0).href)

        expect(mockGroup.getName()).andReturn(properties.groups.items.get(0).name)
        expect(mockGroup.getHref()).andReturn(properties.groups.items.get(0).href)

        expect(mockGroup.getName()).andReturn(properties.groups.items.get(0).name)
        expect(mockGroup.getHref()).andReturn(properties.groups.items.get(0).href)

        expect(mockGroup.getName()).andReturn(properties.groups.items.get(0).name)
        expect(mockGroup.getName()).andReturn(properties.groups.items.get(0).name)
        expect(mockGroup.getName()).andReturn(properties.groups.items.get(0).name)

        expect(mockGroup.getName()).andReturn(properties.groups.items.get(0).name)
        expect(mockGroup.getHref()).andReturn(properties.groups.items.get(0).href)

        expect(mockGroup.getName()).andReturn(properties.groups.items.get(0).name)
        expect(mockGroup.getHref()).andReturn(properties.groups.items.get(0).href)

        expect(mockGroup.getName()).andReturn(properties.groups.items.get(0).name)
        expect(mockGroup.getHref()).andReturn(properties.groups.items.get(0).href)

        expect(mockGroup.getName()).andReturn(properties.groups.items.get(0).name)
        expect(mockGroup.getHref()).andReturn(properties.groups.items.get(0).href)

        expect(mockGroup.getName()).andReturn(properties.groups.items.get(0).name)
        expect(mockGroup.getHref()).andReturn(properties.groups.items.get(0).href)

        replay internalDataStore, mockGroup

        assertFalse(defaultAccount.isMemberOfGroup(groupName.substring(0, groupName.length()-2) + "*")) //fooNa*
        assertFalse(defaultAccount.isMemberOfGroup("*" + groupName.toUpperCase() + "*")) //*FOONAME*
        assertFalse(defaultAccount.isMemberOfGroup("*" + groupName.substring(2, groupName.length()).toLowerCase())) //*oname
        assertTrue(defaultAccount.isMemberOfGroup(groupName)) //fooName
        assertTrue(defaultAccount.isMemberOfGroup(groupName.toUpperCase())) //FOONAME
        assertTrue(defaultAccount.isMemberOfGroup(groupName.toLowerCase())) //fooname
        assertFalse(defaultAccount.isMemberOfGroup(null))
        assertFalse(defaultAccount.isMemberOfGroup(""))
        assertFalse(defaultAccount.isMemberOfGroup("foo"))
        assertFalse(defaultAccount.isMemberOfGroup("*Poo*"))
        assertTrue(defaultAccount.isMemberOfGroup(groupHref.toLowerCase()))
        assertFalse(defaultAccount.isMemberOfGroup(groupHref.substring(0, groupHref.length() - 1) + "*")) //href having last character replaced by wildcard
        assertFalse(defaultAccount.isMemberOfGroup(groupHref.substring(1, groupHref.length()))) //href having first character removed

        verify internalDataStore, mockGroup
    }


    @Test
    void testMissingProviderDataHref() {
        //this scenario should never happen as Hrefs are obtained from the backend when a directory is retrieved
        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf",
                name: "My Directory",
                description: "My Description",
                accounts: [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/accounts"],
                groups: [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/groups"],
                tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"],
                providerData: [createdAt: "2014-04-18T21:32:19.651Z"]
        ]

        DefaultAccount defaultAccount = new DefaultAccount(internalDataStore, properties)
        try {
            defaultAccount.getProviderData()
            fail("Should have thrown")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "providerData resource does not contain its required href property.")
        }
    }

    @Test
    void testIncompatibleProviderDataType() {
        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf",
                name: "My Directory",
                description: "My Description",
                accounts: [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/accounts"],
                groups: [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/groups"],
                tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"],
                providerData: []
        ]

        DefaultAccount defaultAccount = new DefaultAccount(internalDataStore, properties)

        try {
            defaultAccount.getProviderData()
            fail("Should have thrown")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "'providerData' property value type does not match the specified type. Specified type: interface com.stormpath.sdk.provider.ProviderData.  Existing type: java.util.ArrayList.  Value: []")
        }
    }


    @Test
    void testGetNullProviderData() {
        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf",
                name: "My Directory",
                description: "My Description",
                accounts: [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/accounts"],
                groups: [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/groups"],
                tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"]
        ]

        DefaultAccount defaultAccount = new DefaultAccount(internalDataStore, properties)

        assertNull(defaultAccount.getProviderData())
    }

}
