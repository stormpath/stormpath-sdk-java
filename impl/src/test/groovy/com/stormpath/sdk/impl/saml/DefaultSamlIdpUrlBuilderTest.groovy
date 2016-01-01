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
package com.stormpath.sdk.impl.saml

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.lang.Strings
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import org.joda.time.DateTime
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Test for SamlIdpUrlBuilder class
 *
 * @since 1.0.RC8
 */
public class DefaultSamlIdpUrlBuilderTest {

    String apiKeyId
    String apiKeySecret

    @BeforeTest
    void setup() {
        apiKeyId = "3RLOQCNCD1M3T5MAZDNDHGE5"
        apiKeySecret = "g6soSmqihFHnpjKDGBDHKwKR8Q2BwL88gHlZ1t4xJf6"
    }

    @Test
    void testMissingRedirectUri() {
        def internalDataStore = createStrictMock(InternalDataStore)
        def samlProviderEndpoint = "https://api.stormpath.com/v1/directories/jefoifj93riu23ioj/provider"

        def applicationHref = "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj"

        def builder = new DefaultSamlIdpUrlBuilder(internalDataStore, applicationHref, samlProviderEndpoint)
        try {
            builder.build()
            fail("should have thrown")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "callbackUri cannot be null or empty.")
        }
        try {
            builder.setCallbackUri(null)
            builder.build()
            fail("should have thrown")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "callbackUri cannot be null or empty.")
        }
        try {
            builder.setCallbackUri("")
            builder.build()
            fail("should have thrown")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "callbackUri cannot be null or empty.")
        }
    }

    @Test
    void testOnlyCallbackUri() {

        def internalDataStore = createStrictMock(InternalDataStore)
        def apiKey = createStrictMock(ApiKey)
        def samlProviderEndpoint = "https://api.stormpath.com/v1/directories/jefoifj93riu23ioj/provider"

        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getId()).andReturn(apiKeyId)
        expect(apiKey.getSecret()).andReturn(apiKeySecret)
        expect(apiKey.getId()).andReturn(apiKeyId)

        replay internalDataStore, apiKey

        def builder = new DefaultSamlIdpUrlBuilder(internalDataStore, "https://test.stormpath.io/v1/applications/jefoifj93riu23ioj", samlProviderEndpoint)

        def ssoRedirectUrl = builder.setCallbackUri("http://samlIdp:8081/index.do").build()

        assertTrue ssoRedirectUrl.startsWith(samlProviderEndpoint)

        String jwt = ssoRedirectUrl.substring(samlProviderEndpoint.concat("?accessToken=").length())

        assertClaims(jwt, [iss   : apiKeyId, sub: "https://test.stormpath.io/v1/applications/jefoifj93riu23ioj",
                cb_uri: "http://samlIdp:8081/index.do"], ["usd", "sof", "path", "state", "onk"])

        verify internalDataStore, apiKey
    }

    @Test
    void testBuilderWithPathAndState() {

        def internalDataStore = createStrictMock(InternalDataStore)
        def apiKey = createStrictMock(ApiKey)
        def samlProviderEndpoint = "https://api.stormpath.com/v1/directories/jefoifj93riu23ioj/provider"

        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getId()).andReturn(apiKeyId)
        expect(apiKey.getSecret()).andReturn(apiKeySecret)
        expect(apiKey.getId()).andReturn(apiKeyId)

        replay internalDataStore, apiKey

        def builder = new DefaultSamlIdpUrlBuilder(internalDataStore, "https://enterprise.stormpath.io/v1/applications/jefoifj93riu23ioj", samlProviderEndpoint)

        def ssoRedirectUrl = builder.setCallbackUri("http://fooUrl:8081/index.do").setPath("/sso-site").setState("someState").build()

        assertTrue ssoRedirectUrl.startsWith(samlProviderEndpoint)

        String jwt = ssoRedirectUrl.substring(samlProviderEndpoint.concat("?accessToken=").length())

        assertClaims(jwt, [iss   : apiKeyId, sub: "https://enterprise.stormpath.io/v1/applications/jefoifj93riu23ioj",
                cb_uri: "http://fooUrl:8081/index.do", path: "/sso-site", state: "someState"], ["usd", "sof", "onk"])

        verify internalDataStore, apiKey
    }

    @Test
    void testBuilderWithOrganization() {

        def internalDataStore = createStrictMock(InternalDataStore)
        def apiKey = createStrictMock(ApiKey)
        def samlProviderEndpoint = "https://api.stormpath.com/v1/directories/jefoifj93riu23ioj/provider"

        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getId()).andReturn(apiKeyId)
        expect(apiKey.getSecret()).andReturn(apiKeySecret)
        expect(apiKey.getId()).andReturn(apiKeyId)

        replay internalDataStore, apiKey

        def builder = new DefaultSamlIdpUrlBuilder(internalDataStore, "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj", samlProviderEndpoint)

        def ssoRedirectUrl = builder.setCallbackUri("http://fooUrl:8081/index.do").setOrganizationNameKey("my-organization").build()

        assertTrue ssoRedirectUrl.startsWith(samlProviderEndpoint)

        String jwt = ssoRedirectUrl.substring(samlProviderEndpoint.concat("?accessToken=").length())

        assertClaims(jwt, [iss   : apiKeyId, sub: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                cb_uri: "http://fooUrl:8081/index.do", onk: "my-organization"], ["path", "state"])

        verify internalDataStore, apiKey
    }

    @Test
    void testBuilderWithSpToken() {

        def internalDataStore = createStrictMock(InternalDataStore)
        def apiKey = createStrictMock(ApiKey)
        def samlProviderEndpoint = "https://api.stormpath.com/v1/directories/jefoifj93riu23ioj/provider"

        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getId()).andReturn(apiKeyId)
        expect(apiKey.getSecret()).andReturn(apiKeySecret)
        expect(apiKey.getId()).andReturn(apiKeyId)

        replay internalDataStore, apiKey

        def builder = new DefaultSamlIdpUrlBuilder(internalDataStore, "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj", samlProviderEndpoint)

        def ssoRedirectUrl = builder.setCallbackUri("http://fooUrl:8081/index.do").setOrganizationNameKey("my-organization").setSpToken("anSpToken").addProperty("unknown", "xyx").build()

        assertTrue ssoRedirectUrl.startsWith(samlProviderEndpoint)

        String jwt = ssoRedirectUrl.substring(samlProviderEndpoint.concat("?accessToken=").length())

        assertClaims(jwt, [iss   : apiKeyId, sub: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj", sp_token: "anSpToken", unknown: "xyx",
                cb_uri: "http://fooUrl:8081/index.do", onk: "my-organization"], ["path", "state"])

        verify internalDataStore, apiKey
    }

    @Test
    void testBuilderWithPropertiesToOverride() {

        def internalDataStore = createStrictMock(InternalDataStore)
        def apiKey = createStrictMock(ApiKey)
        def samlProviderEndpoint = "https://api.stormpath.com/v1/directories/jefoifj93riu23ioj/provider"

        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getId()).andReturn(apiKeyId)
        expect(apiKey.getSecret()).andReturn(apiKeySecret)
        expect(apiKey.getId()).andReturn(apiKeyId)

        replay internalDataStore, apiKey

        def builder = new DefaultSamlIdpUrlBuilder(internalDataStore, "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj", samlProviderEndpoint)

        def iat = new DateTime().minusHours(1).secondOfMinute().roundFloorCopy().toDate()

        def jti = "aUniqueId"

        def iss = "me"

        def ssoRedirectUrl = builder.setCallbackUri("http://fooUrl:8081/index.do").addProperty("iat", iat).addProperty("jti", jti).addProperty("iss", iss).addProperty("unknown", "xyx").build()

        assertTrue ssoRedirectUrl.startsWith(samlProviderEndpoint)

        String jwt = ssoRedirectUrl.substring(samlProviderEndpoint.concat("?accessToken=").length())

        Claims claims = parseClaims(jwt)

        assertClaims(claims, [iss   : apiKeyId, sub: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj", unknown: "xyx",
                cb_uri: "http://fooUrl:8081/index.do"], ["path", "state", "usd", "onk", "sp_token", "sof"])

        assertNotNull claims.getId()
        assertNotEquals jti, claims.getId()

        assertNotNull claims.getIssuedAt()
        assertNotEquals iat, claims.getIssuedAt()

        assertNotNull claims.getIssuer()
        assertNotEquals iss, claims.getIssuer()

        verify internalDataStore, apiKey
    }


    @Test
    void testBuilderForLogout() {

        def internalDataStore = createStrictMock(InternalDataStore)
        def apiKey = createStrictMock(ApiKey)
        def samlProviderEndpoint = "https://api.stormpath.com/v1/directories/jefoifj93riu23ioj/provider"

        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getId()).andReturn(apiKeyId)
        expect(apiKey.getSecret()).andReturn(apiKeySecret)
        expect(apiKey.getId()).andReturn(apiKeyId)

        replay internalDataStore, apiKey

        def builder = new DefaultSamlIdpUrlBuilder(internalDataStore, "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj", samlProviderEndpoint)

        def ssoRedirectUrl = builder.setCallbackUri("http://fooUrl:8081/index.do").setPath("/sso-site")
                .setState("someState").build()

        assertTrue ssoRedirectUrl.startsWith(samlProviderEndpoint)

        String jwt = ssoRedirectUrl.substring(samlProviderEndpoint.concat("?accessToken=").length())

        assertClaims(parseClaims(jwt), [iss   : apiKeyId, sub: "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj",
                cb_uri: "http://fooUrl:8081/index.do", path: "/sso-site", state: "someState"], ["onk", "usd", "sof"])

        verify internalDataStore, apiKey
    }

    private void assertClaims(String jwt, Map expected, List notExpected) {
        Claims claims = parseClaims(jwt)
        assertClaims(claims, expected, notExpected)
    }

    private static void assertClaims(Claims claims, Map expected, List notExpected) {
        assertTrue Strings.hasText(claims.getId())
        assertNotNull claims.getIssuedAt()
        expected.each { k, v -> assertEquals v, claims["${k}"] }
        notExpected.each { k -> assertFalse claims.containsKey("${k}") }
    }

    private Claims parseClaims(String jwt) {
        Jws<Claims> jws = Jwts.parser().setSigningKey(apiKeySecret.bytes).parseClaimsJws(jwt)

        validateHeader(jws.header)
        jws.body
    }

    private static void validateHeader(JwsHeader header) {
        assertEquals header.size(), 3
        assertEquals header.alg, "HS256"
        assertEquals header.typ, Header.JWT_TYPE
    }
}

