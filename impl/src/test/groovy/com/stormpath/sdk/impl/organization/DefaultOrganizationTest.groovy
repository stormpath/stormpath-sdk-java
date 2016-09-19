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
package com.stormpath.sdk.impl.organization

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.stormpath.sdk.account.AccountLinkingPolicy
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.impl.tenant.DefaultTenant
import com.stormpath.sdk.organization.CreateOrganizationRequest
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.organization.OrganizationStatus
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test
import java.text.DateFormat

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.RC7
 */
class DefaultOrganizationTest {

    @Test
    void testGetPropertyDescriptors() {

        DefaultOrganization defaultOrganization = new DefaultOrganization(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultOrganization.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 10)

        assertTrue(propertyDescriptors.get("name") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("nameKey") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("description") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("customData") instanceof ResourceReference && propertyDescriptors.get("customData").getType().equals(CustomData))
        //since 1.1.0
        assertTrue(propertyDescriptors.get("accountLinkingPolicy") instanceof ResourceReference && propertyDescriptors.get("accountLinkingPolicy").getType().equals(AccountLinkingPolicy))

    }

    @Test
    void testMethods() {

        def properties = [href: "https://api.stormpath.com/v1/organizations/jefoifj93riu23ioj",
                tenant: [href: "https://api.stormpath.com/v1/tenants/zaef0wq38ruojoiadE"],
                createdAt: "2015-01-01T00:00:00Z",
                modifiedAt: "2015-02-01T00:00:00Z"]

        def internalDataStore = createStrictMock(InternalDataStore)

        def defaultOrg = new DefaultOrganization(internalDataStore, properties)

        assertNull(defaultOrg.getStatus())

        defaultOrg = defaultOrg.setStatus(OrganizationStatus.DISABLED)
                .setName("Org Name")
                .setDescription("Org Description")

        assertEquals(defaultOrg.getStatus(), OrganizationStatus.DISABLED)
        assertEquals(defaultOrg.getName(), "Org Name")
        assertEquals(defaultOrg.getDescription(), "Org Description")

        DateFormat df = new ISO8601DateFormat();
        assertEquals(df.format(defaultOrg.getCreatedAt()), "2015-01-01T00:00:00Z")
        assertEquals(df.format(defaultOrg.getModifiedAt()), "2015-02-01T00:00:00Z")

        expect(internalDataStore.instantiate(Tenant, properties.tenant)).andReturn(new DefaultTenant(internalDataStore, properties.tenant))

        expect(internalDataStore.delete(defaultOrg))

        replay internalDataStore

        def resource = defaultOrg.getTenant()
        assertTrue(resource instanceof DefaultTenant && resource.getHref().equals(properties.tenant.href))

        defaultOrg.delete()

        verify internalDataStore
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testCreateOrganizationWithNullArgument() {
        def internalDataStore = createStrictMock(InternalDataStore)
        def defaultTenant = new DefaultTenant(internalDataStore)
        defaultTenant.createOrganization((CreateOrganizationRequest) null)
    }

    @Test
    void testCreateOrganization() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def organization = createStrictMock(Organization)
        def returnedOrganization = createStrictMock(Organization)

        def defaultTenant = new DefaultTenant(internalDataStore)

        expect(internalDataStore.create("/organizations", organization)).andReturn(returnedOrganization)

        replay internalDataStore, organization, returnedOrganization

        assertEquals(defaultTenant.createOrganization(organization), returnedOrganization)

        verify internalDataStore, organization, returnedOrganization
    }


//    @Test
//    void testSetDefaultAccountStore() {
//
//        def properties = [href: "https://api.stormpath.com/v1/organizations/jefoifj93riu23ioj",
//                tenant: [href: "https://api.stormpath.com/v1/tenants/faef0wq38ruojoiadE"],
//                accounts: [href: "https://api.stormpath.com/v1/organizations/jefoifj93riu23ioj/accounts"],
//                groups: [href: "https://api.stormpath.com/v1/organizations/jefoifj93riu23ioj/groups"],
//                defaultAccountStoreMapping: [href: "https://api.stormpath.com/v1/organizationAccountStoreMappings/5dc0HbVMB8g3GWpSkOzqfF"],
//                defaultGroupStoreMapping: [href: "https://api.stormpath.com/v1/organizationAccountStoreMappings/5dc0HbVMB8g3GWpSkOzqfF"],
//                organizationAccountStoreMappings: [href: "https://api.stormpath.com/v1/organizations/jefoifj93riu23ioj/organizationAccountStoreMappings"]
//        ]
//
//        def accountStoreHref = "https://api.stormpath.com/v1/directories/6i2DiJWcsG6ZyUA8r0EwQU"
//        def groupHref = "https://api.stormpath.com/v1/groups/6dWPIXEi4MvGTnQcWApizf"
//
//        def dataStore = createStrictMock(InternalDataStore)
//        def accountStore = createStrictMock(AccountStore)
//        def group = createStrictMock(Group)
//        def organizationAccountStoreMappings = createStrictMock(OrganizationAccountStoreMappingList)
//        def iterator = createStrictMock(Iterator)
//        def accountStoreMapping = createStrictMock(OrganizationAccountStoreMapping)
//        def newOrganizationAccountStoreMapping = createStrictMock(OrganizationAccountStoreMapping)
//        def requestExecutor = createStrictMock(RequestExecutor)
//
//        def organization = new DefaultOrganization(dataStore, properties)
//
//        // First execution
//        expect(dataStore.instantiate(OrganizationAccountStoreMappingList, properties.organizationAccountStoreMappings)).andReturn(organizationAccountStoreMappings)
//        expect(organizationAccountStoreMappings.iterator()).andReturn(iterator)
//        expect(iterator.hasNext()).andReturn(true)
//        expect(iterator.next()).andReturn(accountStoreMapping)
//        expect(accountStoreMapping.getAccountStore()).andReturn(accountStore)
//        expect(accountStore.getHref()).andReturn(accountStoreHref)
//        expect(accountStoreMapping.setDefaultAccountStore(true)).andReturn(accountStoreMapping)
//
//        expect(group.getHref()).andReturn(groupHref)
//        expect(iterator.hasNext()).andReturn(false)
//        expect(dataStore.instantiate(OrganizationAccountStoreMapping)).andReturn(newOrganizationAccountStoreMapping)
//        expect(newOrganizationAccountStoreMapping.setAccountStore(group)).andReturn(newOrganizationAccountStoreMapping)
//        expect(newOrganizationAccountStoreMapping.setOrganization(organization)).andReturn(newOrganizationAccountStoreMapping)
//        expect(newOrganizationAccountStoreMapping.setListIndex(Integer.MAX_VALUE)).andReturn(newOrganizationAccountStoreMapping)
//        expect(dataStore.create("/organizationAccountStoreMappings", newOrganizationAccountStoreMapping)).andReturn(newOrganizationAccountStoreMapping)
//        expect(newOrganizationAccountStoreMapping.setDefaultAccountStore(true)).andReturn(newOrganizationAccountStoreMapping)
//        expect(newOrganizationAccountStoreMapping.save())
//
//        expect(dataStore.save((Organization) reportMatcher(new OrganizationMatcher(organization))))
//        replay dataStore, accountStore, group, organizationAccountStoreMappings, iterator, accountStoreMapping, newOrganizationAccountStoreMapping, requestExecutor
//
//        organization.setDefaultAccountStore(group)
//
//        organization = new DefaultOrganization(dataStore, properties)
//
//
//
//
//        //Second execution
//        expect(dataStore.instantiate(OrganizationAccountStoreMappingList, properties.organizationAccountStoreMappings)).andReturn(organizationAccountStoreMappings)
//        expect(organizationAccountStoreMappings.iterator()).andReturn(iterator)
//        expect(iterator.hasNext()).andReturn(true)
//        expect(iterator.next()).andReturn(accountStoreMapping)
//        expect(accountStoreMapping.getAccountStore()).andReturn(accountStore)
//        expect(accountStore.getHref()).andReturn(accountStoreHref)
//        expect(iterator.hasNext()).andReturn(false)
//        expect(dataStore.instantiate(OrganizationAccountStoreMapping)).andReturn(newOrganizationAccountStoreMapping)
//        expect(newOrganizationAccountStoreMapping.setAccountStore(group)).andReturn(newOrganizationAccountStoreMapping)
//        expect(newOrganizationAccountStoreMapping.setAccountStore(group)).andReturn(newOrganizationAccountStoreMapping)
//        expect(newOrganizationAccountStoreMapping.setListIndex(Integer.MAX_VALUE)).andReturn(newOrganizationAccountStoreMapping)
//        expect(dataStore.create("/organizationAccountStoreMappings", newOrganizationAccountStoreMapping)).andReturn(newOrganizationAccountStoreMapping)
//        expect(newOrganizationAccountStoreMapping.setDefaultAccountStore(true)).andReturn(newOrganizationAccountStoreMapping)
//        expect(newOrganizationAccountStoreMapping.save())
//        expect(accountStoreMapping.setDefaultAccountStore(true)).andReturn(accountStoreMapping)
//        expect(accountStoreMapping.setOrganization(organization)).andReturn(accountStoreMapping)
//        expect(accountStoreMapping.save())
//
//        replay dataStore, accountStore, group, organizationAccountStoreMappings, iterator, accountStoreMapping, newOrganizationAccountStoreMapping, requestExecutor
//
//        organization.setDefaultAccountStore(accountStore)
//
////        modifiedOrg = new DefaultOrganization(dataStore, properties)
////        expect(dataStore.save((Organization) reportMatcher(new OrganizationMatcher(modifiedOrg))))
////
////
////        expect(accountStoreMapping.setOrganization(modifiedOrg)).andReturn(accountStoreMapping)
////
////
////        replay dataStore, accountStore, group, organizationAccountStoreMappings, iterator, accountStoreMapping, newOrganizationAccountStoreMapping, requestExecutor
////
////
////        def org = new DefaultOrganization(dataStore, properties)
////        org.setDefaultAccountStore(group)
////        org.setDefaultAccountStore(accountStore)
//
//        verify dataStore, accountStore, group, organizationAccountStoreMappings, iterator, accountStoreMapping, newOrganizationAccountStoreMapping, requestExecutor
//    }
//
//    static class OrganizationMatcher implements IArgumentMatcher {
//
//        private Organization expected
//
//        OrganizationMatcher(Organization organization) {
//            expected = organization;
//
//        }
//        boolean matches(Object o) {
//            if (o == null || ! Organization.isInstance(o)) {
//                return false;
//            }
//            Organization actual = (Organization) o
//            return expected.toString().equals(actual.toString())
//        }
//
//        void appendTo(StringBuffer stringBuffer) {
//            stringBuffer.append(expected.toString())
//        }
//    }
//
//    @Test
//    void testSetDefaultGroupStore() {
//
//        def properties = [href: "https://api.stormpath.com/v1/organizations/jefoifj93riu23ioj",
//                tenant: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE"],
//                accounts: [href: "https://api.stormpath.com/v1/organizations/jefoifj93riu23ioj/accounts"],
//                groups: [href: "https://api.stormpath.com/v1/organizations/jefoifj93riu23ioj/groups"],
//                passwordResetTokens: [href: "https://api.stormpath.com/v1/organizations/jefoifj93riu23ioj/passwordResetTokens"],
//                defaultAccountStoreMapping: [href: "https://api.stormpath.com/v1/organizationAccountStoreMappings/5dc0HbVMB8g3GWpSkOzqfF"],
//                defaultGroupStoreMapping: [href: "https://api.stormpath.com/v1/organizationAccountStoreMappings/5dc0HbVMB8g3GWpSkOzqfF"],
//                accountStoreMappings: [href: "https://api.stormpath.com/v1/organizations/jefoifj93riu23ioj/organizationAccountStoreMappings"]
//        ]
//
//        def accountStoreHref = "https://api.stormpath.com/v1/directories/6i2DiJWcsG6ZyUA8r0EwQU"
//        def groupHref = "https://api.stormpath.com/v1/groups/6dWPIXEi4MvGTnQcWApizf"
//
//        def dataStore = createStrictMock(InternalDataStore)
//        def accountStore = createStrictMock(AccountStore)
//        def group = createStrictMock(Group)
//        def organizationAccountStoreMappings = createStrictMock(OrganizationAccountStoreMappingList)
//        def iterator = createStrictMock(Iterator)
//        def accountStoreMapping = createStrictMock(OrganizationAccountStoreMapping)
//        def newOrganizationAccountStoreMapping = createStrictMock(OrganizationAccountStoreMapping)
//        def requestExecutor = createStrictMock(RequestExecutor)
//
//        expect(dataStore.instantiate(OrganizationAccountStoreMappingList, properties.organizationAccountStoreMappings)).andReturn(organizationAccountStoreMappings)
//        expect(organizationAccountStoreMappings.iterator()).andReturn(iterator)
//        expect(iterator.hasNext()).andReturn(true)
//        expect(iterator.next()).andReturn(accountStoreMapping)
//        expect(accountStoreMapping.getAccountStore()).andReturn(accountStore)
//        expect(accountStore.getHref()).andReturn(accountStoreHref)
//        expect(group.getHref()).andReturn(groupHref)
//        expect(iterator.hasNext()).andReturn(false)
//        expect(dataStore.instantiate(OrganizationAccountStoreMapping)).andReturn(newOrganizationAccountStoreMapping)
//        expect(newOrganizationAccountStoreMapping.setAccountStore(group)).andReturn(newOrganizationAccountStoreMapping)
//
//        def modifiedApp = new DefaultOrganization(dataStore, properties)
//        expect(dataStore.save((Organization) reportMatcher(new OrganizationMatcher(modifiedApp))))
//
//        //Second execution
//        expect(dataStore.instantiate(OrganizationAccountStoreMappingList, properties.organizationAccountStoreMappings)).andReturn(organizationAccountStoreMappings)
//        expect(organizationAccountStoreMappings.iterator()).andReturn(iterator)
//        expect(iterator.hasNext()).andReturn(true)
//        expect(iterator.next()).andReturn(accountStoreMapping)
//        expect(accountStoreMapping.getAccountStore()).andReturn(accountStore)
//        expect(accountStore.getHref()).andReturn(accountStoreHref) times 2
//        expect(accountStoreMapping.setDefaultGroupStore(true)).andReturn(accountStoreMapping)
//        expect(accountStoreMapping.save())
//
//        modifiedApp = new DefaultOrganization(dataStore, properties)
//        expect(dataStore.save((Organization) reportMatcher(new OrganizationMatcher(modifiedApp))))
//
//        replay dataStore, accountStore, group, organizationAccountStoreMappings, iterator, accountStoreMapping, newOrganizationAccountStoreMapping, requestExecutor
//
//        def app = new DefaultOrganization(dataStore, properties)
//        app.setDefaultGroupStore(group)
//        app.setDefaultGroupStore(accountStore)
//
//        verify dataStore, accountStore, group, organizationAccountStoreMappings, iterator, accountStoreMapping, newOrganizationAccountStoreMapping, requestExecutor
//    }
}