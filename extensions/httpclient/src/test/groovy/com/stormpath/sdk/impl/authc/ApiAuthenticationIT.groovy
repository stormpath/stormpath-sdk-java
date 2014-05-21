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
package com.stormpath.sdk.impl.authc

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountStatus
import com.stormpath.sdk.api.ApiKeyStatus
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.error.authc.DisabledAccountException
import com.stormpath.sdk.error.authc.DisabledApiKeyException
import com.stormpath.sdk.error.authc.IncorrectCredentialsException
import com.stormpath.sdk.error.authc.InvalidApiKeyException
import com.stormpath.sdk.error.authc.MissingApiKeyException
import com.stormpath.sdk.error.authc.UnsupportedAuthenticationSchemeException
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.http.HttpRequestBuilder
import com.stormpath.sdk.http.HttpRequests
import com.stormpath.sdk.impl.oauth.http.OauthHttpServletRequest
import com.stormpath.sdk.impl.util.Base64
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * Class ApiAuthenticationIT is used for testing Api Authentication methods.
 *
 * @since 1.0.RC
 */
class ApiAuthenticationIT extends ClientIT {

    Application application

    Account account

    @BeforeMethod
    void initiateTest() {
        application = createTempApp()
        account = createTestAccount(application)
    }

    @Test
    void testBasicAuthentication() {

        def apiKey = account.createApiKey()

        def headers = createHttpHeaders(createBasicAuthzHeader(apiKey.id, apiKey.secret), "application/x-www-form-urlencoded")

        HttpRequestBuilder httpRequestBuilder = HttpRequests.method(HttpMethod.GET).headers(headers)

        verifySuccessfulAuthentication(httpRequestBuilder.build(), account, DefaultApiAuthenticationResult)

        def newApiKey = account.createApiKey()

        httpRequestBuilder.headers(createHttpHeaders(createBasicAuthzHeader(newApiKey.id, newApiKey.secret), null))

        verifySuccessfulAuthentication(httpRequestBuilder.build(), account, DefaultApiAuthenticationResult)

        apiKey.setStatus(ApiKeyStatus.DISABLED)
        apiKey.save()

        Map parameters = convertToParametersMap([:])

        httpRequestBuilder.headers(createHttpHeaders(createBasicAuthzHeader(apiKey.id, apiKey.secret), null)).parameters(parameters)

        OauthHttpServletRequest servletRequest = new OauthHttpServletRequest(httpRequestBuilder.build())

        verifyError(servletRequest, DisabledApiKeyException)

        httpRequestBuilder.headers(createHttpHeaders(createBasicAuthzHeader(newApiKey.id, newApiKey.secret), null))

        servletRequest = new OauthHttpServletRequest(httpRequestBuilder.build())

        verifySuccessfulAuthentication(servletRequest, account, DefaultApiAuthenticationResult)
    }

    @Test
    void testApiKeyAuthenticationErrors() {

        def apiKey = account.createApiKey()

        HttpRequestBuilder httpRequestBuilder = HttpRequests.method(HttpMethod.GET).headers(["content-type": convertToArray("application/json")])

        verifyError(httpRequestBuilder.build(), MissingApiKeyException)

        httpRequestBuilder.headers(["Authorization": convertToArray("this-is-not-correct-cred")])

        verifyError(httpRequestBuilder.build(), MissingApiKeyException)

        httpRequestBuilder.headers(["Authorization": convertToArray("SAuthc MyUserAnd")])

        verifyError(httpRequestBuilder.build(), UnsupportedAuthenticationSchemeException)

        httpRequestBuilder.headers(["Authorization": convertToArray("Basic @#%^&*")])

        verifyError(httpRequestBuilder.build(), InvalidApiKeyException)

        httpRequestBuilder.headers(["Authorization": convertToArray("Basic this-is-not-correct")])

        verifyError(httpRequestBuilder.build(), InvalidApiKeyException)

        httpRequestBuilder.headers(createHttpHeaders(createBasicAuthzHeader(apiKey.id, ""), null))

        verifyError(httpRequestBuilder.build(), IncorrectCredentialsException)

        httpRequestBuilder.headers(createHttpHeaders(createBasicAuthzHeader(apiKey.id, "wrong-value"), null))

        verifyError(httpRequestBuilder.build(), IncorrectCredentialsException)

        httpRequestBuilder.headers(createHttpHeaders(createBasicAuthzHeader(apiKey.id, apiKey.secret), null))

        verifySuccessfulAuthentication(httpRequestBuilder.build(), account, DefaultApiAuthenticationResult)

        apiKey.setStatus(ApiKeyStatus.DISABLED)
        apiKey.save()

        verifyError(httpRequestBuilder.build(), DisabledApiKeyException)

        apiKey.setStatus(ApiKeyStatus.ENABLED)
        apiKey.save()

        account.setStatus(AccountStatus.DISABLED)
        account.save()

        verifyError(httpRequestBuilder.build(), DisabledAccountException)
    }

    void verifySuccessfulAuthentication(Object httpRequest, Account expectedAccount, Class expectedResultClass) {

        def authResult = application.authenticate(httpRequest).execute()

        assertTrue expectedResultClass.isAssignableFrom(authResult.class)

        assertEquals expectedAccount.href, authResult.account.href

    }

    void verifyError(Object httpRequest, Class exceptionClass) {
        try {
            application.authenticate(httpRequest).execute()
            fail("ResourceException: " + exceptionClass.toString() + "was expected")
        } catch (com.stormpath.sdk.resource.ResourceException exception) {
            assertTrue exceptionClass.isAssignableFrom(exception.class);
        }
    }

    def static String[] convertToArray(String value) {

        String[] array = [value]

        array
    }

    def static Map createHttpHeaders(String authzHeader, String contentType) {

        def headers = [:]
        String[] authzHeaderArray = [authzHeader]

        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/json"
        }

        String[] contentTypeArray = [contentType]
        headers.put("content-type", contentTypeArray)

        headers.put("authorization", authzHeaderArray)

        headers
    }

    def static Map convertToParametersMap(Map<String, String> inputMap) {

        Map<String, String[]> result = new HashMap<>()

        for (Map.Entry<String, String> entry : inputMap.entrySet()) {

            String[] value = [entry.value]

            result.put(entry.key, value)
        }

        result
    }

    def static String createBasicAuthzHeader(String id, String secret) {

        String cred = id + ":" + secret

        byte[] bytes = cred.getBytes("UTF-8")

        "Basic " + Base64.encodeToString(bytes, false)
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

}
