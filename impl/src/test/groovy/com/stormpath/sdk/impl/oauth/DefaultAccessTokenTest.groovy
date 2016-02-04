/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.oauth

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.impl.account.DefaultAccount
import com.stormpath.sdk.impl.application.DefaultApplication
import com.stormpath.sdk.impl.resource.DateProperty
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.ResourceReference
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.impl.tenant.DefaultTenant
import com.stormpath.sdk.oauth.AccessToken
import com.stormpath.sdk.tenant.Tenant
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.testng.annotations.Test

import java.text.DateFormat

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Test for AccessToken class
 *
 * @since 1.0.RC7
 */
class DefaultAccessTokenTest {

    @Test
    void testMethods() {

        DateFormat df = new ISO8601DateFormat();

        def secret = "a_very_secret_key"
        def href = "https://api.stormpath.com/v1/accessTokens/5hFj6FUwNb28OQrp93phPP"

        // no rti claim means it's not a valid access token
        String jwt = Jwts.builder()
            .setSubject(href)
            .claim("rti", "abcdefg")
            .signWith(SignatureAlgorithm.HS256, secret.getBytes("UTF-8"))
            .compact();

        def properties = [
            href: href,
            jwt: jwt,
            tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"],
            application: [href: "https://api.stormpath.com/v1/applications/928glsjeorigjj09etiij"],
            account: [href: "https://api.stormpath.com/v1/accounts/apsd98f2kj09etiij"],
            created_at: "2015-01-01T00:00:00Z"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def apiKey = createStrictMock(ApiKey)

        expect(apiKey.getSecret()).andReturn(secret)

        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(internalDataStore.instantiate(Tenant, properties.tenant)).andReturn(new DefaultTenant(internalDataStore, properties.tenant))
        expect(internalDataStore.instantiate(Account, properties.account)).andReturn(new DefaultAccount(internalDataStore, properties.account))
        expect(internalDataStore.instantiate(Application, properties.application)).andReturn(new DefaultApplication(internalDataStore, properties.application))

        replay internalDataStore
        replay apiKey

        def defaultAccessToken = new DefaultAccessToken(internalDataStore, properties)

        assertEquals(defaultAccessToken.getHref(), properties.href)
        assertEquals(defaultAccessToken.getJwt(), properties.jwt)
        assertEquals(df.format(defaultAccessToken.getCreatedAt()), "2015-01-01T00:00:00Z", properties.created_at)

        def tenant = defaultAccessToken.getTenant()
        assertTrue(tenant instanceof Tenant && tenant.getHref().equals(properties.tenant.href))

        def account = defaultAccessToken.getAccount()
        assertTrue(account instanceof Account && account.getHref().equals(properties.account.href))

        def application = defaultAccessToken.getApplication()
        assertTrue(application instanceof Application && application.getHref().equals(properties.application.href))
    }

    @Test
    void testInvalidAccessToken() {
        def secret = "a_very_secret_key"
        def href = "https://api.stormpath.com/v1/accessTokens/5hFj6FUwNb28OQrp93phPP"

        // no rti claim means it's not a valid access token
        String jwt = Jwts.builder()
            .setSubject(href)
            .signWith(SignatureAlgorithm.HS256, secret.getBytes("UTF-8"))
            .compact();

        def properties = [
            href: href,
            jwt: jwt
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def apiKey = createStrictMock(ApiKey)

        expect(apiKey.getSecret()).andReturn(secret)
        expect(internalDataStore.getApiKey()).andReturn(apiKey)

        replay internalDataStore
        replay apiKey

        try {
            new DefaultAccessToken(internalDataStore, properties)
            fail("should have thrown")
        } catch (Exception e) {
            def message = e.getMessage()
            assertTrue message.equals("JWT failed validation; it cannot be trusted.")
        }
    }

    @Test
    void testValidAccessToken() {
        def secret = "a_very_secret_key"
        def href = "https://api.stormpath.com/v1/accessTokens/5hFj6FUwNb28OQrp93phPP"

        String jwt = Jwts.builder()
            .setSubject(href)
            .claim("rti", "abcdefg")
            .signWith(SignatureAlgorithm.HS512, secret.getBytes("UTF-8"))
            .compact();

        def properties = [
            href: href,
            jwt: jwt
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def apiKey = createStrictMock(ApiKey)

        expect(apiKey.getSecret()).andReturn(secret)
        expect(internalDataStore.getApiKey()).andReturn(apiKey)

        replay internalDataStore
        replay apiKey

        AccessToken accessToken = new DefaultAccessToken(internalDataStore, properties)
        assertEquals(accessToken.getHref(), href)
    }

}
