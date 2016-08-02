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
package com.stormpath.sdk.servlet.tenant

import com.stormpath.sdk.accountStoreMapping.AccountStoreMapping
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList
import com.stormpath.sdk.directory.AccountStoreVisitor
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.servlet.organization.DefaultOrganizationNameKeyResolver
import com.stormpath.sdk.servlet.util.SubdomainResolver
import org.easymock.Capture
import org.easymock.IAnswer
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals

/**
 * @since 1.0.0
 */
class DefaultTenantResolverTest {

    @Test
    void testOrganizationExistInAccountStores() {
        def request = createMock(HttpServletRequest)
        def application = createStrictMock(Application)
        def applicationAccountStoreMappingList = createStrictMock(ApplicationAccountStoreMappingList)
        def iterator = createMock(Iterator)
        def accountStoreMapping = createMock(AccountStoreMapping)
        def dir = createMock(Directory)
        def organization = createMock(Organization)

        expect(request.getAttribute(Application.getCanonicalName())).andReturn(application)
        expect(request.getHeader(eq('Host'))).andStubReturn('bar.foo.com')
        expect(application.getAccountStoreMappings()).andReturn(applicationAccountStoreMappingList)
        expect(applicationAccountStoreMappingList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountStoreMapping)
        expect(accountStoreMapping.getAccountStore()).andReturn(dir)
        expect(dir.accept(anyObject())).andVoid()
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountStoreMapping)
        expect(accountStoreMapping.getAccountStore()).andReturn(organization)

        Capture capturedArgument = new Capture<AccountStoreVisitor>();
        expect(organization.accept(and(capture(capturedArgument), isA(AccountStoreVisitor)))).andAnswer(
                new IAnswer<AccountStoreVisitor>() {
                    @Override
                    public AccountStoreVisitor answer() throws Throwable {
                        AccountStoreVisitor accountStoreVisitor = (AccountStoreVisitor) capturedArgument.getValue();
                        accountStoreVisitor.visit(organization);
                    }
                }
        )
        expect(organization.getName()).andReturn("bar")  //we return "bar" as the organization name found in account store mappings

        replay(request, application, applicationAccountStoreMappingList, iterator, accountStoreMapping, dir, organization)

        def organizationNameKeyResolver = new DefaultOrganizationNameKeyResolver();
        organizationNameKeyResolver.setSubdomainResolver(new SubdomainResolver())
        def resolver = new DefaultTenantResolver();
        resolver.setOrganizationNameKeyResolver(organizationNameKeyResolver)

        assertEquals(resolver.get(request, null), organization) //expected Organization is 'organization'

        verify(request, application, applicationAccountStoreMappingList, iterator, accountStoreMapping, dir, organization)
    }

    @Test
    void testOrganizationDoesNotExistInAccountStores() {
        def request = createMock(HttpServletRequest)
        def application = createStrictMock(Application)
        def applicationAccountStoreMappingList = createStrictMock(ApplicationAccountStoreMappingList)
        def iterator = createMock(Iterator)
        def accountStoreMapping = createMock(AccountStoreMapping)
        def group = createMock(Group)

        expect(request.getAttribute(Application.getCanonicalName())).andReturn(application)
        expect(request.getHeader(eq('Host'))).andStubReturn('bar.foo.com')
        expect(application.getAccountStoreMappings()).andReturn(applicationAccountStoreMappingList)
        expect(applicationAccountStoreMappingList.iterator()).andReturn(iterator)
        expect(iterator.hasNext()).andReturn(true)
        expect(iterator.next()).andReturn(accountStoreMapping)
        expect(accountStoreMapping.getAccountStore()).andReturn(group)
        expect(group.accept(anyObject())).andVoid()
        expect(iterator.hasNext()).andReturn(false)

        replay(request, application, applicationAccountStoreMappingList, iterator, accountStoreMapping, group)

        def organizationNameKeyResolver = new DefaultOrganizationNameKeyResolver();
        organizationNameKeyResolver.setSubdomainResolver(new SubdomainResolver())
        def resolver = new DefaultTenantResolver();
        resolver.setOrganizationNameKeyResolver(organizationNameKeyResolver)

        assertEquals(resolver.get(request, null), null)  //expected Organization is null

        verify(request, application, applicationAccountStoreMappingList, iterator, accountStoreMapping, group)
    }

    @Test
    void testNoOrganizationDomain() {
        def request = createMock(HttpServletRequest)

        expect(request.getHeader(eq('Host'))).andStubReturn('foo.com') //no organization found in url

        replay(request)

        def organizationNameKeyResolver = new DefaultOrganizationNameKeyResolver();
        organizationNameKeyResolver.setSubdomainResolver(new SubdomainResolver())
        def resolver = new DefaultTenantResolver();
        resolver.setOrganizationNameKeyResolver(organizationNameKeyResolver)

        assertEquals(resolver.get(request, null), null)

        verify(request)
    }

}
