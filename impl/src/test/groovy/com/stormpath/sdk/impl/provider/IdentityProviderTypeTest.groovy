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
package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.provider.*
import com.stormpath.sdk.provider.saml.SamlProvider
import com.stormpath.sdk.provider.saml.SamlProviderData
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
class IdentityProviderTypeTest {

    @Test
    void testMaps() {

        final int PROVIDER_COUNT = 6;

        assertEquals(IdentityProviderType.IDENTITY_PROVIDER_MAP.size(), PROVIDER_COUNT)
        IdentityProviderType item = IdentityProviderType.IDENTITY_PROVIDER_MAP.get("stormpath");
        assertEquals(item.getNameKey(), "stormpath")
        assertEquals(item.getProviderClass(), Provider.class)
        assertEquals(item.getProviderDataClass(), ProviderData.class)

        item = IdentityProviderType.IDENTITY_PROVIDER_MAP.get("facebook");
        assertEquals(item.getNameKey(), "facebook")
        assertEquals(item.getProviderClass(), FacebookProvider.class)
        assertEquals(item.getProviderDataClass(), FacebookProviderData.class)

        item = IdentityProviderType.IDENTITY_PROVIDER_MAP.get("google");
        assertEquals(item.getNameKey(), "google")
        assertEquals(item.getProviderClass(), GoogleProvider.class)
        assertEquals(item.getProviderDataClass(), GoogleProviderData.class)

        item = IdentityProviderType.IDENTITY_PROVIDER_MAP.get("github");
        assertEquals(item.getNameKey(), "github")
        assertEquals(item.getProviderClass(), GithubProvider.class)
        assertEquals(item.getProviderDataClass(), GithubProviderData.class)

        item = IdentityProviderType.IDENTITY_PROVIDER_MAP.get("linkedin");
        assertEquals(item.getNameKey(), "linkedin")
        assertEquals(item.getProviderClass(), LinkedInProvider.class)
        assertEquals(item.getProviderDataClass(), LinkedInProviderData.class)

        item = IdentityProviderType.IDENTITY_PROVIDER_MAP.get("saml");
        assertEquals(item.getNameKey(), "saml")
        assertEquals(item.getProviderClass(), SamlProvider.class)
        assertEquals(item.getProviderDataClass(), SamlProviderData.class)

        assertEquals(IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP.size(), PROVIDER_COUNT)
        assertNotNull(IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP.get("stormpath"))
        assertNotNull(IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP.get("facebook"))
        assertNotNull(IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP.get("google"))
        assertNotNull(IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP.get("github"))
        assertNotNull(IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP.get("linkedin"))
        assertNotNull(IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP.get("saml"))

        assertEquals(IdentityProviderType.IDENTITY_PROVIDERDATA_CLASS_MAP.size(), PROVIDER_COUNT)
        assertNotNull(IdentityProviderType.IDENTITY_PROVIDERDATA_CLASS_MAP.get("stormpath"))
        assertNotNull(IdentityProviderType.IDENTITY_PROVIDERDATA_CLASS_MAP.get("facebook"))
        assertNotNull(IdentityProviderType.IDENTITY_PROVIDERDATA_CLASS_MAP.get("google"))
        assertNotNull(IdentityProviderType.IDENTITY_PROVIDERDATA_CLASS_MAP.get("github"))
        assertNotNull(IdentityProviderType.IDENTITY_PROVIDERDATA_CLASS_MAP.get("linkedin"))
        assertNotNull(IdentityProviderType.IDENTITY_PROVIDERDATA_CLASS_MAP.get("saml"))
    }

    @Test
    void testFromNameKey() {
        assertSame(IdentityProviderType.fromNameKey("STormPATh"), IdentityProviderType.STORMPATH)
        assertSame(IdentityProviderType.fromNameKey("facebook"), IdentityProviderType.FACEBOOK)
        assertSame(IdentityProviderType.fromNameKey("GOOGLE"), IdentityProviderType.GOOGLE)
        assertSame(IdentityProviderType.fromNameKey("GitHUB"), IdentityProviderType.GITHUB)
        assertSame(IdentityProviderType.fromNameKey("LiNKEdIn"), IdentityProviderType.LINKEDIN)
    }

    @Test
    void testFromNonexistentNameKey() {
        assertNull(IdentityProviderType.fromNameKey(null))

        try{
            IdentityProviderType.fromNameKey("nonexistentNameKey")
            fail("should have thrown")
        } catch (Exception e) {
            assertEquals(e.getMessage(), "The nameKey provided doesn't match a valid IdentityProviderType: nonexistentNameKey")
        }
    }


}
