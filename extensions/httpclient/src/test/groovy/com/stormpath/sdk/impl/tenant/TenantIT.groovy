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
package com.stormpath.sdk.impl.tenant

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.provider.FacebookProvider
import com.stormpath.sdk.provider.GoogleProvider
import com.stormpath.sdk.provider.Providers
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 0.8
 */
class TenantIT extends ClientIT {

    //TODO - enable this test after assertions are made with the Cache statistics API.
    // This currently just prints output and isn't a valid test for asserting caching.
    @Test(enabled = false)
    void testCaching() {

        //create a bunch of apps:
        def tenant = client.currentTenant

        /*
        120.times { i ->
            Application app = client.instantiate(Application)
            app.name = "Testing Application ${i+1} " + UUID.randomUUID().toString()

            Application created =
                tenant.createApplication(Applications.newCreateRequestFor(app).createDirectory().build())

            resourcesToDelete.add(created);
        }*/

        int count = 0;
        def hrefs = []
        def list = tenant.getApplications()
        for( Application app : list) {
            hrefs << app.href
            count++
        }
        println "Applications seen: $count"

        hrefs.each { href ->
            client.getResource(href, Application)
        }

        count = 0
        hrefs = []
        list = tenant.getDirectories()
        for( Directory directory : list) {
            hrefs << directory.href
            count++
        }
        println "Directories seen: $count"

        hrefs.each { href ->
            client.getResource(href, Directory)
        }

        tenant = client.getResource(tenant.getHref(), Tenant)

        list = tenant.getApplications()
        for(Application app : list) {
            println "Application $app.name"
        }
        list = tenant.getDirectories()
        for(Directory dir : list) {
            println "Directory $dir.name"
        }

        def s = client.dataStore.cacheManager.toString()
        println '"cacheManager": ' + s;

        Thread.sleep(20)
    }

    //@since 1.0.beta
    @Test
    void testCreateDirWithoutProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithoutProvider")
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "stormpath")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
    }

    //@since 1.0.beta
    @Test
    void testCreateDirWithGoogleProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithGoogleProvider")
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")

        def request = Directories.newCreateRequestFor(dir).
                forProvider(Providers.GOOGLE.createProviderRequest()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .setRedirectUri("https://www.myAppURL:8090/index.jsp")
                        .build()
                ).build()

        dir = client.currentTenant.createDirectory(request)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "google")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertTrue(GoogleProvider.isAssignableFrom(provider.getClass()))
        assertEquals(((GoogleProvider)provider).getClientId(), clientId)
        assertEquals(((GoogleProvider)provider).getClientSecret(), clientSecret)
        assertEquals(((GoogleProvider)provider).getRedirectUri(), "https://www.myAppURL:8090/index.jsp")
    }

    //@since 1.0.beta
    @Test
    void testCreateDirWithFacebookProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithFacebookProvider")
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")

        def request = Directories.newCreateRequestFor(dir).
                forProvider(Providers.FACEBOOK.createProviderRequest()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .build()
                ).build()

        dir = client.currentTenant.createDirectory(request)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "facebook")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertTrue(FacebookProvider.isAssignableFrom(provider.getClass()))
        assertEquals(((FacebookProvider)provider).getClientId(), clientId)
        assertEquals(((FacebookProvider)provider).getClientSecret(), clientSecret)
    }



}
