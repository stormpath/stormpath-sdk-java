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
package com.stormpath.sdk.impl.sso

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountResult
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.error.jwt.InvalidJwtException
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.http.HttpRequest
import com.stormpath.sdk.http.HttpRequests
import com.stormpath.sdk.impl.http.QueryString
import com.stormpath.sdk.impl.jwt.signer.DefaultJwtSigner
import com.stormpath.sdk.lang.Strings
import org.codehaus.jackson.map.ObjectMapper
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * Class SsoResponseIT is used for to test {@link Application#handleSsoResponse(Object)}
 * method.
 *
 * @since 1.0.RC
 */
class SsoResponseIT extends ClientIT {

    Application application

    Account account

    @BeforeMethod
    void initiateTest() {
        application = createTempApp()
        account = createTestAccount(application)
    }

    @Test
    void testHandleSsoResponse() {

        String jwtResponseValue = buildSsoResponse("https://myapplication.com", 60, "anState")

        QueryString queryString = new QueryString()
        queryString.put("jwtResponse", jwtResponseValue)

        HttpRequest httpRequest = HttpRequests.method(HttpMethod.GET).headers(createHttpHeaders("application/json"))
                .queryParameters(queryString.toString()).build()

        assertAccountResult(httpRequest, "anState")

        jwtResponseValue = buildSsoResponse("https://myapplication.com", 60, null)

        queryString = new QueryString()
        queryString.put("jwtResponse", jwtResponseValue)

        httpRequest = HttpRequests.method(HttpMethod.GET).headers(createHttpHeaders("application/json"))
                .queryParameters(queryString.toString()).build()

        assertAccountResult(httpRequest, null)
    }

    @Test
    void testHandleSsoResponseError() {

        String jwtResponseValue = buildSsoResponse("https://myapplication.com", 60, "thisIs the expected state")

        QueryString queryString = new QueryString()
        queryString.put("jwtResponse", jwtResponseValue)

        //Use it with in a HTTP POST (Only GET is supported).
        HttpRequest httpRequest = HttpRequests.method(HttpMethod.POST).headers(createHttpHeaders("application/json"))
                .queryParameters(jwtResponseValue).build()

        assertHandleSsoError(httpRequest, IllegalArgumentException, "")

        httpRequest = HttpRequests.method(HttpMethod.GET).headers(createHttpHeaders("application/json"))
                .queryParameters(queryString.toString()).build()

        //First time must succeed
        assertAccountResult(httpRequest, "thisIs the expected state")

        //Use it a second time
        assertHandleSsoError(httpRequest, InvalidJwtException, InvalidJwtException.ALREADY_USED_JWT_ERROR)

        //Let the jwtExpired (ttl 2 seconds)
        jwtResponseValue = buildSsoResponse("https://myapplication.com", 2, "thisIs will expire")
        queryString.put("jwtResponse", jwtResponseValue)

        //Wait 3 seconds
        Thread.sleep(3000)

        httpRequest = HttpRequests.method(HttpMethod.GET).headers(createHttpHeaders("application/json"))
                .queryParameters(queryString.toString()).build()

        assertHandleSsoError(httpRequest, InvalidJwtException, InvalidJwtException.EXPIRED_JWT_ERROR)
    }

    void assertHandleSsoError(Object httpRequest, Class<? extends Exception> expectedException, String expectedMessage) {
        try {
            application.handleSsoResponse(httpRequest).execute()
            fail("Test failed, exception was expected. Exception expected: " + expectedException.getName())
        } catch (Exception ex) {
            assertTrue expectedException.isAssignableFrom(ex.getClass())
            if (Strings.hasText(expectedMessage)) {
                assertEquals ex.getMessage(), expectedMessage
            }
        }
    }

    void assertAccountResult(Object httpRequest, String expectedState) {

        AccountResult result = application.handleSsoResponse(httpRequest).execute()

        assertNotNull result
        assertNotNull result.account
        assertEquals result.account.href, account.href
        assertEquals result.account.email, account.email

        if (Strings.hasText(expectedState)) {
            assertEquals result.state, expectedState
        } else {
            assertNull result.state
        }

        assertFalse result.newAccount
    }

    String buildSsoResponse(String issuer, int ttl, String state) {

        def apiKeyId = client.dataStore.apiKey.id

        def expired = (System.currentTimeMillis() / 1000) + ttl

        def nonce = UUID.randomUUID().toString()

        def map = [sub: account.href, isNewSub: false, iss: issuer, aud: apiKeyId, exp: expired, irt: nonce]

        if (Strings.hasText(state)) {
            map.state = state
        }

        ObjectMapper objectMapper = new ObjectMapper()

        DefaultJwtSigner signer = new DefaultJwtSigner(client.dataStore.apiKey.secret)

        signer.sign(objectMapper.writeValueAsString(map))
    }

    def Account createTestAccount(Application app) {

        def email = 'deleteme@nowhere.com'

        Account account = client.instantiate(Account)
        account.givenName = 'John'
        account.surname = 'DELETEME'
        account.email = email
        account.password = 'Changeme1!'

        app.createAccount(account)
        deleteOnTeardown(account)

        return account
    }

    def static Map createHttpHeaders(String contentType) {

        def headers = [:]

        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/json"
        }

        String[] contentTypeArray = [contentType]
        headers.put("content-type", contentTypeArray)

        headers
    }
}
