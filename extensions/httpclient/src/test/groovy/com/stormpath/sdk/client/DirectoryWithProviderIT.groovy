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
package com.stormpath.sdk.client;

import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.provider.FacebookProvider
import com.stormpath.sdk.provider.GoogleProvider
import org.testng.annotations.Test
import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
public class DirectoryWithProviderIT extends ClientIT {

    @Test
    void testCreateDirWithoutProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryWithProviderIT.testCreateDirWithoutProvider")
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "stormpath")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
    }

    @Test
    void testCreateDirWithGoogleProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryWithProviderIT.testCreateDirWithGoogleProvider")
        GoogleProvider provider = client.instantiate(GoogleProvider.class);
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
        provider.setClientId(clientId).setClientSecret(clientSecret).setRedirectUri("https://www.myAppURL:8090/index.jsp");
        dir.setProvider(provider)
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "google")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertEquals(provider.getClientId(), clientId)
        assertEquals(provider.getClientSecret(), clientSecret)
        assertEquals(provider.getRedirectUri(), "https://www.myAppURL:8090/index.jsp")
    }

    @Test
    void testCreateDirWithFacebookProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryWithProviderIT.testCreateDirWithFacebookProvider")
        FacebookProvider provider = client.instantiate(FacebookProvider.class);
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
        provider.setClientId(clientId).setClientSecret(clientSecret)
        dir.setProvider(provider)
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "facebook")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertEquals(provider.getClientId(), clientId)
        assertEquals(provider.getClientSecret(), clientSecret)
    }



}
