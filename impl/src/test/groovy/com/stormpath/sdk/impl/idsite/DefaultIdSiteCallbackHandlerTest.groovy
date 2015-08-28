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
import com.stormpath.sdk.api.ApiKeys
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.error.jwt.InvalidJwtException
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.http.HttpRequest
import com.stormpath.sdk.idsite.*
import com.stormpath.sdk.impl.ds.DefaultDataStore
import com.stormpath.sdk.impl.http.RequestExecutor
import org.testng.annotations.Test

import static com.stormpath.sdk.impl.jwt.JwtConstants.JWR_RESPONSE_PARAM_NAME
import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals

/**
 * @since 1.0.RC3
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

    /* @since 1.0.RC4.6 */
    @Test
    void testIDSiteException() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJraWQiOiIyRVY3MEFIUlRZRjBKT0E3T0VGTzNTTTI5IiwiYWxnIjoiSFMyNTYifQ.eyJlcn" +
                "IiOnsiY29kZSI6MTEwMDEsImRldmVsb3Blck1lc3NhZ2UiOiJUb2tlbiBpcyBpbnZhbGlkIGJlY2F1c2UgdGhlIHNwZWNpZmllZCBv" +
                "cmdhbml6YXRpb24gbmFtZSBrZXkgZG9lcyBub3QgZXhpc3QgaW4geW91ciBTdG9ybXBhdGggVGVuYW50IiwibWVzc2FnZSI6IlRva2" +
                "VuIGlzIGludmFsaWQiLCJtb3JlSW5mbyI6Im1haWx0bzpzdXBwb3J0QHN0b3JtcGF0aC5jb20iLCJzdGF0dXMiOjQwMH0sImlzcyI6" +
                "Imh0dHBzOi8vc3R1cmR5LXNoaWVsZC5pZC5zdG9ybXBhdGguaW8iLCJleHAiOjMzNTAyNDY2NjUwMDAsImlhdCI6IjE0MDcxOTg1NT" +
                "AiLCJqdGkiOiI0MzZ2a2tIZ2sxeDMwNTdwQ1BxVGFoIn0.NORlAh8X4UgkHJtAfTw7rv4nJws1TGDsiKs6kuasI_0"
        testError(jwtResponse, 11001, 400, "Token is invalid", "Token is invalid because the specified organization name key does not exist in your Stormpath Tenant")
    }

    /* @since 1.0.RC4.6 */
    @Test
    void testExpiredIDSiteError() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJraWQiOiIyRVY3MEFIUlRZRjBKT0E3T0VGTzNTTTI5IiwiYWxnIjoiSFMyNTYifQ.eyJlcn" +
                "IiOnsiY29kZSI6MTEwMDEsImRldmVsb3Blck1lc3NhZ2UiOiJUb2tlbiBpcyBpbnZhbGlkIGJlY2F1c2UgdGhlIHNwZWNpZmllZCBv" +
                "cmdhbml6YXRpb24gbmFtZSBrZXkgZG9lcyBub3QgZXhpc3QgaW4geW91ciBTdG9ybXBhdGggVGVuYW50IiwibWVzc2FnZSI6IlRva2" +
                "VuIGlzIGludmFsaWQiLCJtb3JlSW5mbyI6Im1haWx0bzpzdXBwb3J0QHN0b3JtcGF0aC5jb20iLCJzdGF0dXMiOjQwMH0sImlzcyI6" +
                "Imh0dHBzOi8vc3R1cmR5LXNoaWVsZC5pZC5zdG9ybXBhdGguaW8iLCJleHAiOjE0NDA3MDUwNjAsImlhdCI6IjE0MDcxOTg1NTAiLC" +
                "JqdGkiOiI0MzZ2a2tIZ2sxeDMwNTdwQ1BxVGFoIn0.deg4D2JJzmVElaIh8c2NejxQVUtktIJINOfUKNO2FwQ"
        testExpired(jwtResponse)
    }

    /* @since 1.0.RC4.6 */
    @Test
    void testIDSiteExceptionRethrow() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJraWQiOiIyRVY3MEFIUlRZRjBKT0E3T0VGTzNTTTI5IiwiYWxnIjoiSFMyNTYifQ.eyJlcn" +
                "IiOnsiY29kZSI6NDAwLCJkZXZlbG9wZXJNZXNzYWdlIjoiVGhlIHNwZWNpZmllZCBjYWxsYmFjayBVUkkgKGNiX3VyaSkgaXMgbm90" +
                "IHZhbGlkLiBNYWtlIHN1cmUgdGhlIGNhbGxiYWNrIFVSSSBzcGVjaWZpZWQgaW4geW91ciBJRCBTaXRlIGNvbmZpZ3VyYXRpb24gbW" +
                "F0Y2hlcyB0aGUgdmFsdWUgc3BlY2lmaWVkLiIsIm1lc3NhZ2UiOiJUaGUgc3BlY2lmaWVkIGNhbGxiYWNrIFVSSSAoY2JfdXJpKSBp" +
                "cyBub3QgdmFsaWQiLCJtb3JlSW5mbyI6Im1haWx0bzpzdXBwb3J0QHN0b3JtcGF0aC5jb20iLCJzdGF0dXMiOjQwMH0sImlzcyI6Im" +
                "h0dHBzOi8vc3R1cmR5LXNoaWVsZC5pZC5zdG9ybXBhdGguaW8iLCJleHAiOjMzNTAyNDY2NjUwMDAsImlhdCI6IjE0MDcxOTg1NTAi" +
                "LCJqdGkiOiI0MzZ2a2tIZ2sxeDMwNTdwQ1BxVGFoIn0.dfEQs7tNnmfoFh0yiA05IOYyn4khmDV81HOUT8uJ7uA"
        testRethrow(jwtResponse, InvalidIDSiteCallbackURIException.class, 400, 400, "The specified callback URI (cb_uri) is not valid", "The specified callback URI (cb_uri) is not valid. Make sure the callback URI specified in your ID Site configuration matches the value specified.")

        jwtResponse = "eyJ0eXAiOiJKV1QiLCJraWQiOiIyRVY3MEFIUlRZRjBKT0E3T0VGTzNTTTI5IiwiYWxnIjoiSFMyNTYifQ.eyJlcnIiOnsiY" +
                "29kZSI6MTAwMTIsImRldmVsb3Blck1lc3NhZ2UiOiJUb2tlbiBpcyBpbnZhbGlkIGJlY2F1c2UgdGhlIGlzc3VlZCBhdCB0aW1lICh" +
                "pYXQpIGlzIGFmdGVyIHRoZSBjdXJyZW50IHRpbWUiLCJtZXNzYWdlIjoiVG9rZW4gaXMgaW52YWxpZCIsIm1vcmVJbmZvIjoibWFpb" +
                "HRvOnN1cHBvcnRAc3Rvcm1wYXRoLmNvbSIsInN0YXR1cyI6NDAwfSwiaXNzIjoiaHR0cHM6Ly9zdHVyZHktc2hpZWxkLmlkLnN0b3J" +
                "tcGF0aC5pbyIsImV4cCI6MzM1MDI0NjY2NTAwMCwiaWF0IjoiMTQwNzE5ODU1MCIsImp0aSI6IjQzNnZra0hnazF4MzA1N3BDUHFUY" +
                "WgifQ.RYhogfxeUz2mzGhAgYdaNuxC6IzJ6PAPXGi4Ag9nW-M"
        testRethrow(jwtResponse, InvalidIDSiteTokenException.class, 10012, 400, "Token is invalid", "Token is invalid because the issued at time (iat) is after the current time")

        jwtResponse = "eyJ0eXAiOiJKV1QiLCJraWQiOiIyRVY3MEFIUlRZRjBKT0E3T0VGTzNTTTI5IiwiYWxnIjoiSFMyNTYifQ.eyJlcnIiOnsiY" +
                "29kZSI6MTIwMDEsImRldmVsb3Blck1lc3NhZ2UiOiJUaGUgc2Vzc2lvbiBvbiBJRCBTaXRlIGhhcyB0aW1lZCBvdXQuIFRoaXMgY2F" +
                "uIG9jY3VyIGlmIHRoZSB1c2VyIHN0YXlzIG9uIElEIFNpdGUgd2l0aG91dCBsb2dnaW5nIGluLCByZWdpc3RlcmluZywgb3IgcmVzZ" +
                "XR0aW5nIGEgcGFzc3dvcmQuIiwibWVzc2FnZSI6IlRoZSBzZXNzaW9uIG9uIElEIFNpdGUgaGFzIHRpbWVkIG91dC4iLCJtb3JlSW5" +
                "mbyI6Im1haWx0bzpzdXBwb3J0QHN0b3JtcGF0aC5jb20iLCJzdGF0dXMiOjQwMX0sImlzcyI6Imh0dHBzOi8vc3R1cmR5LXNoaWVsZ" +
                "C5pZC5zdG9ybXBhdGguaW8iLCJleHAiOjMzNTAyNDY2NjUwMDAsImlhdCI6IjE0MDcxOTg1NTAiLCJqdGkiOiI0MzZ2a2tIZ2sxeDM" +
                "wNTdwQ1BxVGFoIn0.Ndt6bi1T6CcYZsydM0Z0zLez7ZN_8JIH__vegZbpfj0"
        testRethrow(jwtResponse, IDSiteSessionTimeoutException.class, 12001, 401, "The session on ID Site has timed out.", "The session on ID Site has timed out. This can occur if the user stays on ID Site without logging in, registering, or resetting a password.")
    }

    private void testListener(String jwtResponse, IdSiteResultStatus expectedListenerMethod) {
        def apiKey = ApiKeys.builder().setId('2EV70AHRTYF0JOA7OEFO3SM29').setSecret('goPUHQMkS4dlKwl5wtbNd91I+UrRehCsEDJrIrMruK8').build()
        def requestExecutor = createStrictMock(RequestExecutor)
        def dataStore = new DefaultDataStore(requestExecutor, apiKey)
        def application = createStrictMock(Application)
        def request = createStrictMock(HttpRequest)
        def listener = createStrictMock(IdSiteResultListener)
        def account = createStrictMock(Account)

        AccountResult accountResultFromListener = null

        expect(request.getMethod()).andReturn(HttpMethod.GET)
        expect(request.getParameter(JWR_RESPONSE_PARAM_NAME)).andReturn(jwtResponse)

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

        replay application, request, listener, account

        DefaultIdSiteCallbackHandler callbackHandler = new DefaultIdSiteCallbackHandler(dataStore, application, request)
        callbackHandler.setResultListener(listener)

        AccountResult accountResult = callbackHandler.getAccountResult()
        assertEquals accountResult.account.href, 'https://api.stormpath.com/v1/accounts/7Ora8KfVDEIQP38KzrYdAs'
        assertEquals accountResultFromListener.account.href, 'https://api.stormpath.com/v1/accounts/7Ora8KfVDEIQP38KzrYdAs'
        assertEquals(accountResult.account, accountResultFromListener.account)
        assertEquals(accountResult.newAccount, accountResultFromListener.newAccount)
        assertEquals(accountResult.state, accountResultFromListener.state)

        verify application, request, listener, account
    }

    /* @since 1.0.RC4.6 */
    private static void testError(String jwtResponse, int expectedCode, int expectedStatus, String expectedMessage, String expectedDeveloperMessage) {
        def apiKey = ApiKeys.builder().setId('2EV70AHRTYF0JOA7OEFO3SM29').setSecret('goPUHQMkS4dlKwl5wtbNd91I+UrRehCsEDJrIrMruK8').build()
        def requestExecutor = createStrictMock(RequestExecutor)
        def dataStore = new DefaultDataStore(requestExecutor, apiKey)
        def application = createStrictMock(Application)
        def request = createStrictMock(HttpRequest)

        expect(request.getMethod()).andReturn(HttpMethod.GET)
        expect(request.getParameter(JWR_RESPONSE_PARAM_NAME)).andReturn(jwtResponse)

        replay application, request

        DefaultIdSiteCallbackHandler callbackHandler = new DefaultIdSiteCallbackHandler(dataStore, application, request)

        try {
            callbackHandler.getAccountResult()
            throw new Exception("should have thrown")
        } catch (IDSiteRuntimeException e) {
            assertEquals(e.getStatus(), expectedStatus)
            assertEquals(e.getCode(), expectedCode)
            assertEquals(e.getStormpathError().message, expectedMessage)
            assertEquals(e.getDeveloperMessage(), expectedDeveloperMessage)
        }

        verify application, request
    }

    /* @since 1.0.RC4.6 */
    private static void testExpired(String jwtResponse) {
        def apiKey = ApiKeys.builder().setId('2EV70AHRTYF0JOA7OEFO3SM29').setSecret('goPUHQMkS4dlKwl5wtbNd91I+UrRehCsEDJrIrMruK8').build()
        def requestExecutor = createStrictMock(RequestExecutor)
        def dataStore = new DefaultDataStore(requestExecutor, apiKey)
        def application = createStrictMock(Application)
        def request = createStrictMock(HttpRequest)

        expect(request.getMethod()).andReturn(HttpMethod.GET)
        expect(request.getParameter(JWR_RESPONSE_PARAM_NAME)).andReturn(jwtResponse)

        replay application, request

        DefaultIdSiteCallbackHandler callbackHandler = new DefaultIdSiteCallbackHandler(dataStore, application, request)

        try {
            callbackHandler.getAccountResult()
            throw new Exception("should have thrown")
        } catch (InvalidJwtException e) {
            assertEquals(e.getMessage(), "JWT has already expired.")
        }

        verify application, request
    }

    /* @since 1.0.RC4.6 */
    private static void testRethrow(String jwtResponse, Class<IDSiteException> expectedExceptionClass, int expectedCode, int expectedStatus, String expectedMessage, String expectedDeveloperMessage) {
        def apiKey = ApiKeys.builder().setId('2EV70AHRTYF0JOA7OEFO3SM29').setSecret('goPUHQMkS4dlKwl5wtbNd91I+UrRehCsEDJrIrMruK8').build()
        def requestExecutor = createStrictMock(RequestExecutor)
        def dataStore = new DefaultDataStore(requestExecutor, apiKey)
        def application = createStrictMock(Application)
        def request = createStrictMock(HttpRequest)

        expect(request.getMethod()).andReturn(HttpMethod.GET)
        expect(request.getParameter(JWR_RESPONSE_PARAM_NAME)).andReturn(jwtResponse)

        replay application, request

        DefaultIdSiteCallbackHandler callbackHandler = new DefaultIdSiteCallbackHandler(dataStore, application, request)

        try {
            callbackHandler.getAccountResult()
            throw new Exception("should have thrown")
        } catch (IDSiteRuntimeException ex) {
            try {
                ex.rethrow();
                throw new Exception("should have thrown")
            } catch (InvalidIDSiteCallbackURIException e) {
                assertEquals(e.getClass(), expectedExceptionClass)
                assertEquals(e.getStatus(), expectedStatus)
                assertEquals(e.getCode(), expectedCode)
                assertEquals(e.getStormpathError().message, expectedMessage)
                assertEquals(e.getDeveloperMessage(), expectedDeveloperMessage)
             }catch (InvalidIDSiteTokenException e) {
                assertEquals(e.getClass(), expectedExceptionClass)
                assertEquals(e.getStatus(), expectedStatus)
                assertEquals(e.getCode(), expectedCode)
                assertEquals(e.getStormpathError().message, expectedMessage)
                assertEquals(e.getDeveloperMessage(), expectedDeveloperMessage)
            } catch (IDSiteSessionTimeoutException e) {
                assertEquals(e.getClass(), expectedExceptionClass)
                assertEquals(e.getStatus(), expectedStatus)
                assertEquals(e.getCode(), expectedCode)
                assertEquals(e.getStormpathError().message, expectedMessage)
                assertEquals(e.getDeveloperMessage(), expectedDeveloperMessage)
            } catch (Exception e) {
                throw new Exception("should have thrown")
            }
        }

        verify application, request
    }

}
