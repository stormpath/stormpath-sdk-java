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

import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.impl.directory.DefaultDirectory
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.provider.DefaultGoogleProvider
import com.stormpath.sdk.impl.resource.AbstractResource
import com.stormpath.sdk.provider.Provider
import com.stormpath.sdk.provider.Providers
import org.easymock.IArgumentMatcher
import org.testng.annotations.Test

import java.lang.reflect.Field

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.8
 */
class DefaultTenantTest {

    @Test
    void testCreateDirectory() {

        def properties = [ href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE",
                applications: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/applications"],
                directories: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/directories"]]

        def internalDataStore = createStrictMock(InternalDataStore)

        def directory = createStrictMock(Directory)
        def returnedDirectory = createStrictMock(Directory)

        def defaultTenant = new DefaultTenant(internalDataStore, properties)

        expect(internalDataStore.create("/directories", directory)).andReturn(returnedDirectory)

        replay internalDataStore, directory, returnedDirectory

        assertEquals(defaultTenant.createDirectory(directory), returnedDirectory)

        verify internalDataStore, directory, returnedDirectory
    }

    @Test
    void testCreateDirectoryRequest() {

        def properties = [ href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE",
                applications: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/applications"],
                directories: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/directories"]
        ]

        def providerProperties = [providerId: "google"]

        def internalDataStore = createStrictMock(InternalDataStore)
        def defaultDirectory = createStrictMock(DefaultDirectory)
        def returnedDirectory = createStrictMock(Directory)
        def defaultTenant = new DefaultTenant(internalDataStore, properties)

        def defaultProvider = new DefaultGoogleProvider(null, providerProperties)
        defaultProvider.setClientId("aClientId999").setClientSecret("aClientSecret111").setRedirectUri("http://someUrl:99999")

        expect(internalDataStore.create("/directories", defaultDirectory)).andReturn(returnedDirectory)
        expect(defaultDirectory.setProvider((Provider) reportMatcher(new ProviderEquals(defaultProvider)))).andReturn(defaultDirectory)

        replay internalDataStore, defaultDirectory, returnedDirectory

        def request = Directories.newCreateRequestFor(defaultDirectory).
                forProvider(Providers.GOOGLE.builder()
                        .setClientId("aClientId999")
                        .setClientSecret("aClientSecret111")
                        .setRedirectUri("http://someUrl:99999")
                        .build()
                ).build();

        assertEquals(defaultTenant.createDirectory(request), returnedDirectory)

        verify(internalDataStore, defaultDirectory, returnedDirectory)
    }

    @Test
    void testUnrecognizedCreateDirectoryRequestType() {

        def properties = [ href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE",
                applications: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/applications"],
                directories: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/directories"]
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def defaultDirectory = createStrictMock(Directory)
        def defaultTenant = new DefaultTenant(internalDataStore, properties)

        def request = Directories.newCreateRequestFor(defaultDirectory).
                forProvider(Providers.GOOGLE.builder()
                        .setClientId("aClientId999")
                        .setClientSecret("aClientSecret111")
                        .setRedirectUri("http://someUrl:99999")
                        .build()
                ).build()

        try {
            defaultTenant.createDirectory(request)
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("the directory instance is of an unidentified type. The specified provider cannot be set to it: EasyMock for interface com.stormpath.sdk.directory.Directory"))
        }

    }

    //@since 1.0.beta
    static class ProviderEquals implements IArgumentMatcher {

        private Provider expected

        ProviderEquals(Provider provider) {
            expected = provider;

        }
        boolean matches(Object o) {
            if (o == null || ! Provider.isInstance(o)) {
                return false;
            }
            Provider actual = (Provider) o
            Map actualProperties = getValue(AbstractResource, actual, "properties")
            Map actualDirtyProperties = getValue(AbstractResource, actual, "dirtyProperties")
            Map expectedProperties = getValue(AbstractResource, expected, "properties")
            Map expectedDirtyProperties = getValue(AbstractResource, expected, "dirtyProperties")
            assertEquals(actualProperties, expectedProperties)
            assertEquals(actualDirtyProperties, expectedDirtyProperties)
            return true
        }

        void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(expected.toString())
        }

        private Object getValue(Class clazz, Object object, String fieldName){
            Field field = clazz.getDeclaredField(fieldName)
            field.setAccessible(true)
            return field.get(object)
        }
    }


}