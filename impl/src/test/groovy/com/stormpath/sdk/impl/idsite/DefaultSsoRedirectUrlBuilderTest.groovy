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
package com.stormpath.sdk.impl.idsite

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.ds.JacksonMapMarshaller
import com.stormpath.sdk.impl.util.Base64
import org.testng.annotations.Test

import java.nio.charset.Charset

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.RC
 */
public class DefaultSsoRedirectUrlBuilderTest {

    @Test
    void testMissingRedirectUri() {
        def internalDataStore = createStrictMock(InternalDataStore)

        def applicationHref = "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj"

        def builder = new DefaultIdSiteUrlBuilder(internalDataStore, applicationHref)
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
        def UTF_8 = Charset.forName("UTF-8");

        def internalDataStore = createStrictMock(InternalDataStore)
        def apiKey = createStrictMock(ApiKey)

        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getId()).andReturn("3RLOQCNCD1M3T5MAZDNDHGE5")
        expect(apiKey.getSecret()).andReturn("g6soSmqihFHnpjKDGBDHKwKR8Q2BwL88gHlZ1t4xJf6")

        replay internalDataStore, apiKey

        def builder = new DefaultIdSiteUrlBuilder(internalDataStore, "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj")
        def ssoRedirectUrl = builder.setCallbackUri("http://fooUrl:8081/index.do").build()

        String[] pieces = ssoRedirectUrl.substring(ssoRedirectUrl.indexOf("?jwtRequest=") + 12).split("\\.");
        if (pieces.length != 3) {
            fail("Expected JWT to have 3 segments separated by '.', but it has " + pieces.length + " segments");
        }
        String jwtHeaderSegment = pieces[0];
        String jwtPayloadSegment = pieces[1];
        byte[] signature = Base64.decodeBase64(pieces[2]);

        def mapMarshaller = new JacksonMapMarshaller();

        //Verify Header
        assertEquals(jwtHeaderSegment, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")

        byte[] jsonBytes = Base64.decodeBase64(jwtPayloadSegment);
        Map jsonMap = mapMarshaller.unmarshal(new String(jsonBytes, UTF_8));

        //Verify Payload
        assertEquals(jsonMap.size(), 5)
        assertTrue(((String)jsonMap.get("iat")).size() > 0)
        assertTrue(((String)jsonMap.get("jti")).size() > 0)
        assertEquals((String)jsonMap.get("iss"), "3RLOQCNCD1M3T5MAZDNDHGE5")
        assertEquals((String)jsonMap.get("sub"), "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj")
        assertEquals((String)jsonMap.get("cb_uri"), "http://fooUrl:8081/index.do")

        //Verify Signature
        assertTrue(signature.size() > 0 )

        //Verify URL
        assertTrue ssoRedirectUrl.startsWith(builder.SSO_ENDPOINT + "?jwtRequest=")

        verify internalDataStore, apiKey
    }

    @Test
    void testBuilderWithPathAndState() {
        def UTF_8 = Charset.forName("UTF-8");

        def internalDataStore = createStrictMock(InternalDataStore)
        def apiKey = createStrictMock(ApiKey)

        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getId()).andReturn("3RLOQCNCD1M3T5MAZDNDHGE5")
        expect(apiKey.getSecret()).andReturn("g6soSmqihFHnpjKDGBDHKwKR8Q2BwL88gHlZ1t4xJf6")

        replay internalDataStore, apiKey

        def builder = new DefaultIdSiteUrlBuilder(internalDataStore, "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj")
        def ssoRedirectUrl = builder.setCallbackUri("http://fooUrl:8081/index.do")
                .setPath("/sso-site")
                .setState("someState")
                .build()

        String[] pieces = ssoRedirectUrl.substring(ssoRedirectUrl.indexOf("?jwtRequest=") + 12).split("\\.");
        if (pieces.length != 3) {
            fail("Expected JWT to have 3 segments separated by '.', but it has " + pieces.length + " segments");
        }
        String jwtHeaderSegment = pieces[0];
        String jwtPayloadSegment = pieces[1];
        byte[] signature = Base64.decodeBase64(pieces[2]);

        def mapMarshaller = new JacksonMapMarshaller();

        //Verify Header
        assertEquals(jwtHeaderSegment, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")

        byte[] jsonBytes = Base64.decodeBase64(jwtPayloadSegment);
        Map jsonMap = mapMarshaller.unmarshal(new String(jsonBytes, UTF_8));

        //Verify Payload
        assertEquals(jsonMap.size(), 7)
        assertTrue(((String)jsonMap.get("iat")).size() > 0)
        assertTrue(((String)jsonMap.get("jti")).size() > 0)
        assertEquals((String)jsonMap.get("iss"), "3RLOQCNCD1M3T5MAZDNDHGE5")
        assertEquals((String)jsonMap.get("sub"), "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj")
        assertEquals((String)jsonMap.get("cb_uri"), "http://fooUrl:8081/index.do")
        assertEquals((String)jsonMap.get("path"), "/sso-site")
        assertEquals((String)jsonMap.get("state"), "someState")

        //Verify Signature
        assertTrue(signature.size() > 0 )

        //Verify URL
        assertTrue ssoRedirectUrl.startsWith(builder.SSO_ENDPOINT + "?jwtRequest=")

        verify internalDataStore, apiKey
    }

    // @since 1.0.RC3
    @Test
    void testBuilderForLogout() {
        def UTF_8 = Charset.forName("UTF-8");

        def internalDataStore = createStrictMock(InternalDataStore)
        def apiKey = createStrictMock(ApiKey)

        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getId()).andReturn("3RLOQCNCD1M3T5MAZDNDHGE5")
        expect(apiKey.getSecret()).andReturn("g6soSmqihFHnpjKDGBDHKwKR8Q2BwL88gHlZ1t4xJf6")

        replay internalDataStore, apiKey

        def builder = new DefaultIdSiteUrlBuilder(internalDataStore, "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj")
        def ssoRedirectUrl = builder.setCallbackUri("http://fooUrl:8081/index.do")
                .setPath("/sso-site")
                .setState("someState")
                .forLogout()
                .build()

        String[] pieces = ssoRedirectUrl.substring(ssoRedirectUrl.indexOf("?jwtRequest=") + 12).split("\\.");
        if (pieces.length != 3) {
            fail("Expected JWT to have 3 segments separated by '.', but it has " + pieces.length + " segments");
        }
        String jwtHeaderSegment = pieces[0];
        String jwtPayloadSegment = pieces[1];
        byte[] signature = Base64.decodeBase64(pieces[2]);

        def mapMarshaller = new JacksonMapMarshaller();

        //Verify Header
        assertEquals(jwtHeaderSegment, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")

        byte[] jsonBytes = Base64.decodeBase64(jwtPayloadSegment);
        Map jsonMap = mapMarshaller.unmarshal(new String(jsonBytes, UTF_8));

        //Verify Payload
        assertEquals(jsonMap.size(), 7)
        assertTrue(((String)jsonMap.get("iat")).size() > 0)
        assertTrue(((String)jsonMap.get("jti")).size() > 0)
        assertEquals((String)jsonMap.get("iss"), "3RLOQCNCD1M3T5MAZDNDHGE5")
        assertEquals((String)jsonMap.get("sub"), "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj")
        assertEquals((String)jsonMap.get("cb_uri"), "http://fooUrl:8081/index.do")
        assertEquals((String)jsonMap.get("path"), "/sso-site")
        assertEquals((String)jsonMap.get("state"), "someState")

        //Verify Signature
        assertTrue(signature.size() > 0 )

        //Verify URL
        assertTrue ssoRedirectUrl.startsWith(builder.SSO_ENDPOINT + "/logout?jwtRequest=")

        verify internalDataStore, apiKey
    }

}
