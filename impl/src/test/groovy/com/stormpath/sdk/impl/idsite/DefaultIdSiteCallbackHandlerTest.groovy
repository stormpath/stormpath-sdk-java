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

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.http.HttpRequest
import com.stormpath.sdk.idsite.AccountResult
import com.stormpath.sdk.idsite.AuthenticationResult
import com.stormpath.sdk.idsite.IdSiteResultListener
import com.stormpath.sdk.idsite.LogoutResult
import com.stormpath.sdk.idsite.RegistrationResult
import com.stormpath.sdk.impl.ds.DefaultDataStore
import org.testng.annotations.Test

import static com.stormpath.sdk.impl.jwt.JwtConstants.JWR_RESPONSE_PARAM_NAME
import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals

/**
 * @since 1.0.0
 */
class DefaultIdSiteCallbackHandlerTest {

    @Test
    void testRegisteredListener() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3N0dXJkeS1zaGllbGQuaWQuc3Rvcm1w" +
                "YXRoLmlvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hY2NvdW50cy83T3JhOEtmVkRFSVFQMzhLenJZZEFzIi" +
                "wiYXVkIjoiMkVWNzBBSFJUWUYwSk9BN09FRk8zU00yOSIsImV4cCI6MjUwMjQ2NjY1MDAwLCJpYXQiOjE0MDcxOTg1NTAsImp0aSI6" +
                "IjQzNnZra0hnazF4MzA1N3BDUHFUYWgiLCJpcnQiOiIxZDAyZDMzNS1mYmZjLTRlYTgtYjgzNi04NWI5ZTJhNmYyYTAiLCJpc05ld1" +
                "N1YiI6ZmFsc2UsInN0YXR1cyI6IlJFR0lTVEVSRUQifQ.4_yCiF6Cik2wep3iwyinTTcn5GHAEvCbIezO1aA5Kkk"
        testListener(jwtResponse, IdSiteResultStatus.REGISTERED)
    }

    @Test
    void testAuthenticatedListener() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3N0dXJkeS1zaGllbGQuaWQuc3Rvcm1w" +
                "YXRoLmlvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hY2NvdW50cy83T3JhOEtmVkRFSVFQMzhLenJZZEFzIi" +
                "wiYXVkIjoiMkVWNzBBSFJUWUYwSk9BN09FRk8zU00yOSIsImV4cCI6MjUwMjQ2NjY1MDAwLCJpYXQiOjE0MDcxOTg1NTAsImp0aSI6" +
                "IjQzNnZra0hnazF4MzA1N3BDUHFUYWgiLCJpcnQiOiIxZDAyZDMzNS1mYmZjLTRlYTgtYjgzNi04NWI5ZTJhNmYyYTAiLCJpc05ld1" +
                "N1YiI6ZmFsc2UsInN0YXR1cyI6IkFVVEhFTlRJQ0FURUQifQ.rpp0lsM1JDFeqkrOdwrtYOB1aitnLwhJuH3iaeuLIXY"
        testListener(jwtResponse, IdSiteResultStatus.AUTHENTICATED)
    }

    @Test
    void testLogoutListener() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3N0dXJkeS1zaGllbGQuaWQuc3Rvcm1w" +
                "YXRoLmlvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hY2NvdW50cy83T3JhOEtmVkRFSVFQMzhLenJZZEFzIi" +
                "wiYXVkIjoiMkVWNzBBSFJUWUYwSk9BN09FRk8zU00yOSIsImV4cCI6MjUwMjQ2NjY1MDAwLCJpYXQiOjE0MDcxOTg1NTAsImp0aSI6" +
                "IjQzNnZra0hnazF4MzA1N3BDUHFUYWgiLCJpcnQiOiIxZDAyZDMzNS1mYmZjLTRlYTgtYjgzNi04NWI5ZTJhNmYyYTAiLCJpc05ld1" +
                "N1YiI6ZmFsc2UsInN0YXR1cyI6IkxPR09VVCJ9.T6ClI4znHCElk1gMQoBpVvE9Jc5Vf4BEjrQ0IWvKYIc"
        testListener(jwtResponse, IdSiteResultStatus.LOGOUT)
    }

    private void testListener(String jwtResponse, IdSiteResultStatus expectedListenerMethod) {
        def dataStore = createStrictMock(DefaultDataStore)
        def application = createStrictMock(Application)
        def request = createStrictMock(HttpRequest)
        def listener = createStrictMock(IdSiteResultListener)
        def apiKey = createStrictMock(ApiKey)
        def account = createStrictMock(Account)
        String apiKeyId = "2EV70AHRTYF0JOA7OEFO3SM29"
        String apiKeySecret = "goPUHQMkS4dlKwl5wtbNd91I+UrRehCsEDJrIrMruK8"

        AccountResult accountResultFromListener = null

        expect(request.getMethod()).andReturn(HttpMethod.GET)
        expect(request.getParameter(JWR_RESPONSE_PARAM_NAME)).andReturn(jwtResponse)
        expect(dataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getId()).andReturn(apiKeyId)
        expect(apiKey.getSecret()).andReturn(apiKeySecret)
        expect(dataStore.isCachingEnabled()).andReturn(false).times(2)
        expect(dataStore.instantiate(Account, [href:"https://api.stormpath.com/v1/accounts/7Ora8KfVDEIQP38KzrYdAs"])).andReturn(account) times 2

        if (expectedListenerMethod.equals(IdSiteResultStatus.REGISTERED)) {
            expect(listener.onRegistered(anyObject(AccountResult))).andDelegateTo( new IdSiteResultListener() {
                @Override
                public void onRegistered(RegistrationResult result) {
                    accountResultFromListener = result
                }
                @Override
                public void onAuthenticated(AuthenticationResult result) {
                    throw new UnsupportedOperationException("This method should have not been executed")
                }
                @Override
                public void onLogout(LogoutResult Result) {
                    throw new UnsupportedOperationException("This method should have not been executed")
                }
            })
        } else if (expectedListenerMethod.equals(IdSiteResultStatus.AUTHENTICATED)) {
            expect(listener.onAuthenticated(anyObject(AccountResult))).andDelegateTo( new IdSiteResultListener() {
                @Override
                public void onRegistered(RegistrationResult result) {
                    throw new UnsupportedOperationException("This method should have not been executed")
                }
                @Override
                public void onAuthenticated(AuthenticationResult result) {
                    accountResultFromListener = result
                }
                @Override
                public void onLogout(LogoutResult result) {
                    throw new UnsupportedOperationException("This method should have not been executed")
                }
            })
        } else if (expectedListenerMethod.equals(IdSiteResultStatus.LOGOUT)) {
            expect(listener.onLogout(anyObject(AccountResult))).andDelegateTo( new IdSiteResultListener() {
                @Override
                public void onRegistered(RegistrationResult result) {
                    throw new UnsupportedOperationException("This method should have not been executed")
                }
                @Override
                public void onAuthenticated(AuthenticationResult result) {
                    throw new UnsupportedOperationException("This method should have not been executed")
                }
                @Override
                public void onLogout(LogoutResult result) {
                    accountResultFromListener = result
                }
            })
        }

        replay dataStore, application, request, listener, apiKey, account

        DefaultIdSiteCallbackHandler callbackHandler = new DefaultIdSiteCallbackHandler(dataStore, application, request)
        callbackHandler.setResultListener(listener)
        AccountResult accountResult = callbackHandler.getAccountResult()
        assertEquals(accountResult.account, accountResultFromListener.account)
        assertEquals(accountResult.newAccount, accountResultFromListener.newAccount)
        assertEquals(accountResult.state, accountResultFromListener.state)

        verify dataStore, application, request, listener, apiKey, account

    }

}
