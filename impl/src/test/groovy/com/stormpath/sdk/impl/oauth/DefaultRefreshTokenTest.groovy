/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.oauth

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.impl.account.DefaultAccount
import com.stormpath.sdk.impl.application.DefaultApplication
import com.stormpath.sdk.impl.resource.DateProperty
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.impl.tenant.DefaultTenant
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import java.text.DateFormat

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Test for RefreshToken class
 *
 * @since 1.0.RC7
 */
class DefaultRefreshTokenTest {

    @Test
    void testGetPropertyDescriptors() {

        def defaultRefreshToken = new DefaultRefreshToken(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultRefreshToken.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 5)

        assertTrue(propertyDescriptors.get("jwt") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("created_at") instanceof DateProperty)

        assertTrue(propertyDescriptors.get("tenant") instanceof ResourceReference && propertyDescriptors.get("tenant").getType().equals(Tenant))
        assertTrue(propertyDescriptors.get("application") instanceof ResourceReference && propertyDescriptors.get("application").getType().equals(Application))
        assertTrue(propertyDescriptors.get("account") instanceof ResourceReference && propertyDescriptors.get("account").getType().equals(Account))
    }

    @Test
    void testMethods() {

        DateFormat df = new ISO8601DateFormat();

        def properties = [href: "https://api.stormpath.com/v1/accessTokens/5hFj6FUwNb28OQrp93phPP",
                jwt: "eyJraWQiOiI2UDVKTjRTQVMwOFFIRlhNTzdGNTY4Ukc2IiwiYWxnIjoiSFMyNT",
                tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"],
                application: [href: "https://api.stormpath.com/v1/applications/928glsjeorigjj09etiij"],
                account: [href: "https://api.stormpath.com/v1/accounts/apsd98f2kj09etiij"],
                created_at: "2015-01-01T00:00:00Z"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def defaultRefreshToken = new DefaultRefreshToken(internalDataStore, properties)

        assertEquals(defaultRefreshToken.getHref(), properties.href)
        assertEquals(defaultRefreshToken.getJwt(), properties.jwt)
        assertEquals(df.format(defaultRefreshToken.getCreatedAt()), "2015-01-01T00:00:00Z", properties.created_at)

        expect(internalDataStore.instantiate(Tenant, properties.tenant)).andReturn(new DefaultTenant(internalDataStore, properties.tenant))
        expect(internalDataStore.instantiate(Account, properties.account)).andReturn(new DefaultAccount(internalDataStore, properties.account))
        expect(internalDataStore.instantiate(Application, properties.application)).andReturn(new DefaultApplication(internalDataStore, properties.application))

        replay internalDataStore

        def tenant = defaultRefreshToken.getTenant()
        assertTrue(tenant instanceof Tenant && tenant.getHref().equals(properties.tenant.href))

        def account = defaultRefreshToken.getAccount()
        assertTrue(account instanceof Account && account.getHref().equals(properties.account.href))

        def application = defaultRefreshToken.getApplication()
        assertTrue(application instanceof Application && application.getHref().equals(properties.application.href))
    }
}