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

import com.fasterxml.jackson.databind.ObjectMapper
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.error.jwt.InvalidJwtException
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.http.HttpRequest
import com.stormpath.sdk.http.HttpRequests
import com.stormpath.sdk.idsite.AccountResult
import com.stormpath.sdk.http.QueryString
import com.stormpath.sdk.impl.jwt.signer.DefaultJwtSigner
import com.stormpath.sdk.lang.Assert
import com.stormpath.sdk.lang.Strings
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * Class IdSiteReplyIT is used for to test {@link Application#newIdSiteCallbackHandler(Object)}
 * method.
 *
 * @since 1.0.RC
 */
class IdSiteReplyIT extends ClientIT {

    Application application

    ObjectMapper objectMapper

    Account account

    @BeforeMethod
    void initiateTest() {
        application = createTempApp()
        account = createTestAccount(application)
        objectMapper = new ObjectMapper()
    }

    @Test
    void testHandleSsoResponse() {

        String apiKeyId = client.dataStore.apiKey.id
        String apiKeySecret = client.dataStore.apiKey.secret

        String jwtResponseValue = buildSsoResponse(apiKeyId, apiKeySecret, "https://myapplication.com", 60, "anState", IdSiteResultStatus.AUTHENTICATED)

        QueryString queryString = new QueryString()
        queryString.put("jwtResponse", jwtResponseValue)

        HttpRequest httpRequest = HttpRequests.method(HttpMethod.GET).headers(createHttpHeaders("application/json"))
                .queryParameters(queryString.toString()).build()

        assertAccountResult(httpRequest, "anState")

        jwtResponseValue = buildSsoResponse(apiKeyId, apiKeySecret, "https://myapplication.com", 60, null, IdSiteResultStatus.REGISTERED)

        queryString = new QueryString()
        queryString.put("jwtResponse", jwtResponseValue)

        httpRequest = HttpRequests.method(HttpMethod.GET).headers(createHttpHeaders("application/json"))
                .queryParameters(queryString.toString()).build()

        assertAccountResult(httpRequest, null)
    }

    @Test
    void testHandleSsoResponseError() {

        String apiKeyId = client.dataStore.apiKey.id
        String apiKeySecret = client.dataStore.apiKey.secret

        String jwtResponseValue = buildSsoResponse(apiKeyId, apiKeySecret, "https://myapplication.com", 60, "thisIs the expected state", IdSiteResultStatus.AUTHENTICATED)

        assertHandleSsoError(jwtResponseValue, IllegalArgumentException, null)

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
        jwtResponseValue = buildSsoResponse(apiKeyId, apiKeySecret, "https://myapplication.com", 2, "thisIs will expire", IdSiteResultStatus.AUTHENTICATED)
        queryString.put("jwtResponse", jwtResponseValue)

        //Wait 3 seconds
        Thread.sleep(3000)

        httpRequest = HttpRequests.method(HttpMethod.GET).headers(createHttpHeaders("application/json"))
                .queryParameters(queryString.toString()).build()

        assertHandleSsoError(httpRequest, InvalidJwtException, InvalidJwtException.EXPIRED_JWT_ERROR)

        jwtResponseValue = buildSsoResponse("this-is-an-invalid-key-id", apiKeySecret, "https://myapplication.com", 2, "thisIs will expire", IdSiteResultStatus.AUTHENTICATED)
        queryString.put("jwtResponse", jwtResponseValue)

        httpRequest = HttpRequests.method(HttpMethod.GET).headers(createHttpHeaders("application/xml"))
                .queryParameters(queryString.toString()).build()

        assertHandleSsoError(httpRequest, InvalidJwtException, InvalidJwtException.JWT_RESPONSE_INVALID_APIKEY_ID_ERROR)

        jwtResponseValue = buildSsoResponse(apiKeyId, "invalid-secret", "https://myapplication.com", 2, "thisIs will expire", IdSiteResultStatus.LOGOUT)
        queryString.put("jwtResponse", jwtResponseValue)

        httpRequest = HttpRequests.method(HttpMethod.GET).headers(createHttpHeaders("application/xml"))
                .queryParameters(queryString.toString()).build()

        assertHandleSsoError(httpRequest, InvalidJwtException, InvalidJwtException.INVALID_JWT_SIGNATURE_ERROR)

        //Missing issuer parameter
        jwtResponseValue = buildSsoResponse(apiKeyId, apiKeySecret, "", 2, "thisIs will expire", IdSiteResultStatus.LOGOUT)
        queryString.put("jwtResponse", jwtResponseValue)

        httpRequest = HttpRequests.method(HttpMethod.GET).headers(createHttpHeaders("application/xml"))
                .queryParameters(queryString.toString()).build()

        assertHandleSsoError(httpRequest, InvalidJwtException, InvalidJwtException.JWT_RESPONSE_MISSING_PARAMETER_ERROR)

        httpRequest = HttpRequests.method(HttpMethod.GET).headers(createHttpHeaders("application/xml")).build()

        assertHandleSsoError(httpRequest, InvalidJwtException, InvalidJwtException.JWT_REQUIRED_ERROR)
    }

    void assertHandleSsoError(Object httpRequest, Class<? extends Exception> expectedException, String expectedMessage) {
        try {
            application.newIdSiteCallbackHandler(httpRequest).getAccountResult()
            fail("Test failed, exception was expected. Exception expected: " + expectedException.getName())
        } catch (Exception ex) {
            assertTrue expectedException.isAssignableFrom(ex.getClass())
            if (Strings.hasText(expectedMessage)) {
                assertEquals ex.getMessage(), expectedMessage
            }
        }
    }

    void assertAccountResult(Object httpRequest, String expectedState) {

        AccountResult result = application.newIdSiteCallbackHandler(httpRequest).getAccountResult()

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

    String buildSsoResponse(String apiKeyId, String apiKeySecret, String issuer, int ttl, String state, IdSiteResultStatus status) {
        Assert.hasText(apiKeyId)
        Assert.hasText(apiKeySecret)

        def expired = (System.currentTimeMillis() / 1000) + ttl

        def nonce = UUID.randomUUID().toString()

        def map = [sub: account.href, isNewSub: false, exp: expired, irt: nonce, status: status]

        if (Strings.hasText(issuer)) {
            map.iss = issuer
        }

        if (Strings.hasText(state)) {
            map.state = state
        }

        DefaultJwtSigner signer = new DefaultJwtSigner(apiKeyId, apiKeySecret)

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
