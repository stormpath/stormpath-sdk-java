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

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.impl.application.DefaultApplication
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.impl.tenant.DefaultTenant
import com.stormpath.sdk.oauth.OAuthPolicy
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Test for DefaultOAuthPolicy class
 *
 * @since 1.0.RC7
 */
class DefaultOAuthPolicyTest {

    @Test
    void testGetPropertyDescriptors() {
        OAuthPolicy passwordPolicy = new DefaultOAuthPolicy(createStrictMock(InternalDataStore))

        def propertyDescriptors = passwordPolicy.getPropertyDescriptors()
        assertEquals(propertyDescriptors.size(), 5)

        assertTrue(propertyDescriptors.get("accessTokenTtl") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("refreshTokenTtl") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("tokenEndpoint") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("application") instanceof ResourceReference && propertyDescriptors.get("application").getType().equals(Application))
        assertTrue(propertyDescriptors.get("tenant") instanceof ResourceReference && propertyDescriptors.get("tenant").getType().equals(Tenant))
    }

    @Test
    void testMethods() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/applications/35YM3OwioW9PVtfLOh6q1e/oauth/token",
                accessTokenTtl: "PT30M",
                refreshTokenTtl: "P7D",
                tokenEndpoint: "https://api.stormpath.com/v1/applications/35YM3OwioW9PVtfLOh6q1e/oauth/token",
                application: [href: "https://api.stormpath.com/v1/applications/35YM3OwioW9PVtfLOh6q1e"],
                tenant: [href: "https://api.stormpath.com/v1/tenants/3Tj2L7gxX6NkXtiiLkh1WF"]
        ]

        DefaultOAuthPolicy oauthPolicy = new DefaultOAuthPolicy(internalDataStore, properties)

        expect(internalDataStore.instantiate(Application, properties.application)).
                andReturn(new DefaultApplication(internalDataStore, properties.application))

        expect(internalDataStore.instantiate(Tenant, properties.tenant)).
                andReturn(new DefaultTenant(internalDataStore, properties.tenant))

        replay internalDataStore

        assertEquals(oauthPolicy.getAccessTokenTtl(), properties.accessTokenTtl)
        assertEquals(oauthPolicy.getRefreshTokenTtl(), properties.refreshTokenTtl)
        assertEquals(oauthPolicy.getTokenEndpoint(), properties.tokenEndpoint)

        oauthPolicy = oauthPolicy.setAccessTokenTtl("P28D")
                .setRefreshTokenTtl("P1D")

        assertEquals(oauthPolicy.getAccessTokenTtl(), "P28D")
        assertEquals(oauthPolicy.getRefreshTokenTtl(), "P1D")
        assertEquals(oauthPolicy.getHref(), properties.href)

        def app = oauthPolicy.getApplication()
        assertTrue(app instanceof Application && app.getHref().equals(properties.application.href))

        def tenant = oauthPolicy.getTenant()
        assertTrue(tenant instanceof Tenant && tenant.getHref().equals(properties.tenant.href))

        verify internalDataStore
    }

    @Test
    void testErrors() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/applications/35YM3OwioW9PVtfLOh6q1e/oauth/token",
                accessTokenTtl: "PT30M",
                refreshTokenTtl: "P7D",
                tokenEndpoint: "https://api.stormpath.com/v1/applications/35YM3OwioW9PVtfLOh6q1e/oauth/token",
                application: [href: "https://api.stormpath.com/v1/applications/35YM3OwioW9PVtfLOh6q1e"],
                tenant: [href: "https://api.stormpath.com/v1/tenants/3Tj2L7gxX6NkXtiiLkh1WF"]
        ]

        expect(internalDataStore.instantiate(Tenant, properties.tenant)).
                andReturn(new DefaultTenant(internalDataStore, properties.tenant))

        expect(internalDataStore.instantiate(Application, properties.application)).
                andReturn(new DefaultApplication(internalDataStore, properties.application))

        replay internalDataStore

        DefaultOAuthPolicy oauthPolicy = new DefaultOAuthPolicy(internalDataStore, properties)

        try {
            oauthPolicy.setRefreshTokenTtl(null)
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "refreshTokenTtl cannot be null.")
        }

        try {
            oauthPolicy.setAccessTokenTtl(null) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "accessTokenTtl cannot be null.")
        }
    }
}