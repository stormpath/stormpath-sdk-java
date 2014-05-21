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
import com.stormpath.sdk.impl.util.Base64
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail

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
    void testApiKeyAuthenticationErrors() {

        def apiKey = account.createApiKey()

        HttpRequestBuilder httpRequestBuilder = HttpRequests.method(HttpMethod.GET).headers(["content-type": convertToArray("application/json")])

        verifyError(httpRequestBuilder.build(), MissingApiKeyException)

        httpRequestBuilder.headers(["Authorization" : convertToArray("this-is-not-correct-cred")])

        verifyError(httpRequestBuilder.build(), MissingApiKeyException)

        httpRequestBuilder.headers(["Authorization" : convertToArray("SAuthc MyUserAnd")])

        verifyError(httpRequestBuilder.build(), UnsupportedAuthenticationSchemeException)

        httpRequestBuilder.headers(["Authorization" : convertToArray("Basic @#%^&*")])

        verifyError(httpRequestBuilder.build(), InvalidApiKeyException)

        httpRequestBuilder.headers(["Authorization" : convertToArray("Basic this-is-not-correct")])

        verifyError(httpRequestBuilder.build(), InvalidApiKeyException)

        httpRequestBuilder.headers(createBasicHttpHeaders(createBasicCredentials(apiKey.id, "")))

        verifyError(httpRequestBuilder.build(), IncorrectCredentialsException)

        httpRequestBuilder.headers(createBasicHttpHeaders(createBasicCredentials(apiKey.id, "wrong-value")))

        verifyError(httpRequestBuilder.build(), IncorrectCredentialsException)

        httpRequestBuilder.headers(createBasicHttpHeaders(createBasicCredentials(apiKey.id, apiKey.secret)))

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

    void verifySuccessfulAuthentication(Object httpRequest, Account expectedAccount, Class expectedResultClass){

        def authResult =  application.authenticate(httpRequest).execute()

        assertTrue expectedResultClass.isAssignableFrom(authResult.class)

        assertEquals expectedAccount.href, authResult.account.href

    }

    void verifyError(Object httpRequest, Class exceptionClass) {
        try {
            application.authenticate(httpRequest).execute()
            fail("must throw an exception")
        } catch (com.stormpath.sdk.resource.ResourceException exception) {
            assertTrue exceptionClass.isAssignableFrom(exception.class);
        }
    }

    def String[] convertToArray(String value){

        String[] array = [value]

        array
    }

    def Map createBasicHttpHeaders(String basicCredentials) {

        def headers = [:]
        String[] basicAuthHeader = ["Basic " + basicCredentials]
        headers.put("content-type", "application/json;charset=utf-8")
        headers.put("authorization", basicAuthHeader)

        headers
    }

    def String createBasicCredentials(String id, String secret) {

        String cred = id + ":" + secret

        byte[] bytes = cred.getBytes("UTF-8")

        return Base64.encodeToString(bytes, false)
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
