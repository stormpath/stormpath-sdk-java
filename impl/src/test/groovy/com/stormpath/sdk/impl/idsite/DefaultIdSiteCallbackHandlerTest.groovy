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
package com.stormpath.sdk.impl.idsite

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.api.ApiKeys
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.error.jwt.InvalidJwtException
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.http.HttpRequest
import com.stormpath.sdk.idsite.AccountResult
import com.stormpath.sdk.idsite.AuthenticationResult
import com.stormpath.sdk.idsite.IDSiteException
import com.stormpath.sdk.idsite.IDSiteRuntimeException
import com.stormpath.sdk.idsite.IDSiteSessionTimeoutException
import com.stormpath.sdk.idsite.IdSiteResultListener
import com.stormpath.sdk.idsite.InvalidIDSiteTokenException
import com.stormpath.sdk.idsite.LogoutResult
import com.stormpath.sdk.idsite.NonceStore
import com.stormpath.sdk.idsite.RegistrationResult
import com.stormpath.sdk.impl.api.ApiKeyCredentials
import com.stormpath.sdk.impl.ds.DefaultDataStore
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.ds.cache.CacheResolver
import com.stormpath.sdk.impl.http.RequestExecutor
import org.testng.annotations.Test

import static com.stormpath.sdk.impl.idsite.IdSiteClaims.JWT_RESPONSE
import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals
import static TestIdSiteResultListenerType.*
import static org.testng.Assert.fail

/**
 * @since 1.0.RC3
 */
class DefaultIdSiteCallbackHandlerTest {

    @Test
    void testRegisteredListener() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6IjJFVjcwQUhSVFlGMEpPQTdPRUZPM1NNMjkifQ.eyJpc3" +
                "MiOiJodHRwczovL3N0dXJkeS1zaGllbGQuaWQuc3Rvcm1wYXRoLmlvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92" +
                "MS9hY2NvdW50cy83T3JhOEtmVkRFSVFQMzhLenJZZEFzIiwiZXhwIjoyNTAyNDY2NjUwMDAsImlhdCI6MTQwNzE5ODU1MCwianRpIj" +
                "oiNDM2dmtrSGdrMXgzMDU3cENQcVRhaCIsImlydCI6IjFkMDJkMzM1LWZiZmMtNGVhOC1iODM2LTg1YjllMmE2ZjJhMCIsImlzTmV3" +
                "U3ViIjpmYWxzZSwic3RhdHVzIjoiUkVHSVNURVJFRCJ9.0n-Sp5zDtiOIJ_Zq6IYdrDHoU3i95XPKhlH-2n9ALdg"
        testListener(jwtResponse, IdSiteResultStatus.REGISTERED, SINGLE)
    }

    @Test
    void testAuthenticatedListener() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6IjJFVjcwQUhSVFlGMEpPQTdPRUZPM1NNMjkifQ.eyJpc3" +
                "MiOiJodHRwczovL3N0dXJkeS1zaGllbGQuaWQuc3Rvcm1wYXRoLmlvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92" +
                "MS9hY2NvdW50cy83T3JhOEtmVkRFSVFQMzhLenJZZEFzIiwiZXhwIjoyNTAyNDY2NjUwMDAsImlhdCI6MTQwNzE5ODU1MCwianRpIj" +
                "oiNDM2dmtrSGdrMXgzMDU3cENQcVRhaCIsImlydCI6IjFkMDJkMzM1LWZiZmMtNGVhOC1iODM2LTg1YjllMmE2ZjJhMCIsImlzTmV3" +
                "U3ViIjpmYWxzZSwic3RhdHVzIjoiQVVUSEVOVElDQVRFRCJ9.jR_T2G0obYuVIf-Gxer5pCieglfzwdWfNoMn505-hEw"
        testListener(jwtResponse, IdSiteResultStatus.AUTHENTICATED, SINGLE)
    }

    @Test
    void testLogoutListener() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6IjJFVjcwQUhSVFlGMEpPQTdPRUZPM1NNMjkifQ.eyJpc3" +
                "MiOiJodHRwczovL3N0dXJkeS1zaGllbGQuaWQuc3Rvcm1wYXRoLmlvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92" +
                "MS9hY2NvdW50cy83T3JhOEtmVkRFSVFQMzhLenJZZEFzIiwiZXhwIjoyNTAyNDY2NjUwMDAsImlhdCI6MTQwNzE5ODU1MCwianRpIj" +
                "oiNDM2dmtrSGdrMXgzMDU3cENQcVRhaCIsImlydCI6IjFkMDJkMzM1LWZiZmMtNGVhOC1iODM2LTg1YjllMmE2ZjJhMCIsImlzTmV3" +
                "U3ViIjpmYWxzZSwic3RhdHVzIjoiTE9HT1VUIn0.eQJAg2ils97GdtokcaK4T98CwfizM7ZCmuTqew9gf2Y"
        testListener(jwtResponse, IdSiteResultStatus.LOGOUT, SINGLE)
    }

    /* @since 1.0.RC7.3 */
    @Test
    void testRegisteredListenerMulti() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6IjJFVjcwQUhSVFlGMEpPQTdPRUZPM1NNMjkifQ.eyJpc3" +
                "MiOiJodHRwczovL3N0dXJkeS1zaGllbGQuaWQuc3Rvcm1wYXRoLmlvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92" +
                "MS9hY2NvdW50cy83T3JhOEtmVkRFSVFQMzhLenJZZEFzIiwiZXhwIjoyNTAyNDY2NjUwMDAsImlhdCI6MTQwNzE5ODU1MCwianRpIj" +
                "oiNDM2dmtrSGdrMXgzMDU3cENQcVRhaCIsImlydCI6IjFkMDJkMzM1LWZiZmMtNGVhOC1iODM2LTg1YjllMmE2ZjJhMCIsImlzTmV3" +
                "U3ViIjpmYWxzZSwic3RhdHVzIjoiUkVHSVNURVJFRCJ9.0n-Sp5zDtiOIJ_Zq6IYdrDHoU3i95XPKhlH-2n9ALdg"
        testListener(jwtResponse, IdSiteResultStatus.REGISTERED, MULTI)
    }

    /* @since 1.0.RC7.3 */
    @Test
    void testAuthenticatedListenerMulti() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6IjJFVjcwQUhSVFlGMEpPQTdPRUZPM1NNMjkifQ.eyJpc3" +
                "MiOiJodHRwczovL3N0dXJkeS1zaGllbGQuaWQuc3Rvcm1wYXRoLmlvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92" +
                "MS9hY2NvdW50cy83T3JhOEtmVkRFSVFQMzhLenJZZEFzIiwiZXhwIjoyNTAyNDY2NjUwMDAsImlhdCI6MTQwNzE5ODU1MCwianRpIj" +
                "oiNDM2dmtrSGdrMXgzMDU3cENQcVRhaCIsImlydCI6IjFkMDJkMzM1LWZiZmMtNGVhOC1iODM2LTg1YjllMmE2ZjJhMCIsImlzTmV3" +
                "U3ViIjpmYWxzZSwic3RhdHVzIjoiQVVUSEVOVElDQVRFRCJ9.jR_T2G0obYuVIf-Gxer5pCieglfzwdWfNoMn505-hEw"
        testListener(jwtResponse, IdSiteResultStatus.AUTHENTICATED, MULTI)
    }

    /* @since 1.0.RC7.3 */
    @Test
    void testLogoutListenerMulti() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6IjJFVjcwQUhSVFlGMEpPQTdPRUZPM1NNMjkifQ.eyJpc3" +
                "MiOiJodHRwczovL3N0dXJkeS1zaGllbGQuaWQuc3Rvcm1wYXRoLmlvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92" +
                "MS9hY2NvdW50cy83T3JhOEtmVkRFSVFQMzhLenJZZEFzIiwiZXhwIjoyNTAyNDY2NjUwMDAsImlhdCI6MTQwNzE5ODU1MCwianRpIj" +
                "oiNDM2dmtrSGdrMXgzMDU3cENQcVRhaCIsImlydCI6IjFkMDJkMzM1LWZiZmMtNGVhOC1iODM2LTg1YjllMmE2ZjJhMCIsImlzTmV3" +
                "U3ViIjpmYWxzZSwic3RhdHVzIjoiTE9HT1VUIn0.eQJAg2ils97GdtokcaK4T98CwfizM7ZCmuTqew9gf2Y"
        testListener(jwtResponse, IdSiteResultStatus.LOGOUT, MULTI)
    }

    /* @since 1.0.RC5 */
    @Test
    void testIDSiteException() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJraWQiOiIyRVY3MEFIUlRZRjBKT0E3T0VGTzNTTTI5IiwiYWxnIjoiSFMyNTYifQ.eyJlcn" +
                "IiOnsiY29kZSI6MTEwMDEsImRldmVsb3Blck1lc3NhZ2UiOiJUb2tlbiBpcyBpbnZhbGlkIGJlY2F1c2UgdGhlIHNwZWNpZmllZCBv" +
                "cmdhbml6YXRpb24gbmFtZSBrZXkgZG9lcyBub3QgZXhpc3QgaW4geW91ciBTdG9ybXBhdGggVGVuYW50LiIsIm1lc3NhZ2UiOiJUb2" +
                "tlbiBpcyBpbnZhbGlkIiwibW9yZUluZm8iOiJodHRwOi8vZG9jcy5zdG9ybXBhdGguY29tL2Vycm9ycy8xMDAxMSIsInN0YXR1cyI6" +
                "NDAwfSwiaXNzIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hcHBsaWNhdGlvbnMvMmVxYnlaOHFvMzRlREU0Z1RvMVI5My" +
                "IsImV4cCI6MzM1MDI0NjY2NTAwMCwiaWF0IjoxNDA3MTk4NTUwLCJqdGkiOiI0MzZ2a2tIZ2sxeDMwNTdwQ1BxVGFoIn0.SDf6NM5S" +
                "10fW7OiGwjcAEqWEPU-nd6YDkOZGBmw8G18"
        testError(jwtResponse, 11001, 400, "Token is invalid", "Token is invalid because the specified organization name key does not exist in your Stormpath Tenant.")
    }

    /* @since 1.0.RC5 */
    @Test
    void testExpiredIDSiteError() {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJraWQiOiIyRVY3MEFIUlRZRjBKT0E3T0VGTzNTTTI5IiwiYWxnIjoiSFMyNTYifQ.eyJlcn" +
                "IiOnsiY29kZSI6MTEwMDEsImRldmVsb3Blck1lc3NhZ2UiOiJUb2tlbiBpcyBpbnZhbGlkIGJlY2F1c2UgdGhlIHNwZWNpZmllZCBv" +
                "cmdhbml6YXRpb24gbmFtZSBrZXkgZG9lcyBub3QgZXhpc3QgaW4geW91ciBTdG9ybXBhdGggVGVuYW50LiIsIm1lc3NhZ2UiOiJUb2" +
                "tlbiBpcyBpbnZhbGlkIiwibW9yZUluZm8iOiJodHRwOi8vZG9jcy5zdG9ybXBhdGguY29tL2Vycm9ycy8xMDAxMSIsInN0YXR1cyI6" +
                "NDAwfSwiaXNzIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hcHBsaWNhdGlvbnMvMmVxYnlaOHFvMzRlREU0Z1RvMVI5My" +
                "IsImV4cCI6MTQ0MDcwNTA2MCwiaWF0IjoxNDA3MTk4NTUwLCJqdGkiOiI0MzZ2a2tIZ2sxeDMwNTdwQ1BxVGFoIn0.OR7ho9XsZ7rC" +
                "RYGumvw-SO0UzD2kEXg-janTAkxD_bE"
        testExpired(jwtResponse)
    }

    /* @since 1.0.RC5 */
    @Test
    void testIDSiteExceptionRethrow() {

        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJraWQiOiIyRVY3MEFIUlRZRjBKT0E3T0VGTzNTTTI5IiwiYWxnIjoiSFMyNTYifQ.eyJlcn" +
                "IiOnsiY29kZSI6MTAwMTIsImRldmVsb3Blck1lc3NhZ2UiOiJUb2tlbiBpcyBpbnZhbGlkIGJlY2F1c2UgdGhlIGlzc3VlZCBhdCB0" +
                "aW1lIChpYXQpIGlzIGFmdGVyIHRoZSBjdXJyZW50IHRpbWUuIiwibWVzc2FnZSI6IlRva2VuIGlzIGludmFsaWQiLCJtb3JlSW5mby" +
                "I6Imh0dHA6Ly9kb2NzLnN0b3JtcGF0aC5jb20vZXJyb3JzLzEwMDEyIiwic3RhdHVzIjo0MDB9LCJpc3MiOiJodHRwczovL2FwaS5z" +
                "dG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW9ucy8yZXFieVo4cW8zNGVERTRnVG8xUjkzIiwiZXhwIjozMzUwMjQ2NjY1MDAwLCJpYX" +
                "QiOjE0MDcxOTg1NTAsImp0aSI6IjQzNnZra0hnazF4MzA1N3BDUHFUYWgifQ.JT__dR0jC6fYZv9NYVC4k45mD5fAQfl_l7yElYm5JMk"
        testRethrow(jwtResponse, InvalidIDSiteTokenException.class, 10012, 400, "Token is invalid", "Token is invalid because the issued at time (iat) is after the current time.")

        jwtResponse = "eyJ0eXAiOiJKV1QiLCJraWQiOiIyRVY3MEFIUlRZRjBKT0E3T0VGTzNTTTI5IiwiYWxnIjoiSFMyNTYifQ.eyJlcnIiOnsiY" +
                "29kZSI6MTEwMDMsImRldmVsb3Blck1lc3NhZ2UiOiJUb2tlbiBpcyBpbnZhbGlkIGJlY2F1c2UgdGhlIHNwZWNpZmllZCBvcmdhbml" +
                "6YXRpb24gbmFtZUtleSBpcyBub3Qgb25lIG9mIHRoZSBhcHBsaWNhdGlvbidzIGFzc2lnbmVkIGFjY291bnQgc3RvcmVzLiIsIm1lc" +
                "3NhZ2UiOiJUb2tlbiBpcyBpbnZhbGlkIiwibW9yZUluZm8iOiJodHRwOi8vZG9jcy5zdG9ybXBhdGguY29tL2Vycm9ycy8xMTAwMyI" +
                "sInN0YXR1cyI6NDAwfSwiaXNzIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hcHBsaWNhdGlvbnMvMmVxYnlaOHFvMzRlR" +
                "EU0Z1RvMVI5MyIsImV4cCI6MzM1MDI0NjY2NTAwMCwiaWF0IjoxNDA3MTk4NTUwLCJqdGkiOiI0MzZ2a2tIZ2sxeDMwNTdwQ1BxVGF" +
                "oIn0.rN7yWI1v9IzsOuooe3cC1WKM4vpqB_vsa00mnXvj3nw"
        testRethrow(jwtResponse, InvalidIDSiteTokenException.class, 11003, 400, "Token is invalid", "Token is invalid because the specified organization nameKey is not one of the application's assigned account stores.")

        jwtResponse = "eyJ0eXAiOiJKV1QiLCJraWQiOiIyRVY3MEFIUlRZRjBKT0E3T0VGTzNTTTI5IiwiYWxnIjoiSFMyNTYifQ.eyJlcnIiOnsiY" +
                "29kZSI6MTIwMDEsImRldmVsb3Blck1lc3NhZ2UiOiJUaGUgc2Vzc2lvbiBvbiBJRCBTaXRlIGhhcyB0aW1lZCBvdXQuIFRoaXMgY2F" +
                "uIG9jY3VyIGlmIHRoZSB1c2VyIHN0YXlzIG9uIElEIFNpdGUgd2l0aG91dCBsb2dnaW5nIGluLCByZWdpc3RlcmluZywgb3IgcmVzZ" +
                "XR0aW5nIGEgcGFzc3dvcmQuIiwibWVzc2FnZSI6IlRoZSBzZXNzaW9uIG9uIElEIFNpdGUgaGFzIHRpbWVkIG91dC4iLCJtb3JlSW5" +
                "mbyI6Im1haWx0bzpzdXBwb3J0QHN0b3JtcGF0aC5jb20iLCJzdGF0dXMiOjQwMX0sImlzcyI6Imh0dHBzOi8vYXBpLnN0b3JtcGF0a" +
                "C5jb20vdjEvYXBwbGljYXRpb25zLzJlcWJ5WjhxbzM0ZURFNGdUbzFSOTMiLCJleHAiOjMzNTAyNDY2NjUwMDAsImlhdCI6MTQwNzE" +
                "5ODU1MCwianRpIjoiNDM2dmtrSGdrMXgzMDU3cENQcVRhaCJ9.xuW4L7HPe0M__mVK7jndY6g9Mcnuc1kanw_7bolOK3Y"
        testRethrow(jwtResponse, IDSiteSessionTimeoutException.class, 12001, 401, "The session on ID Site has timed out.", "The session on ID Site has timed out. This can occur if the user stays on ID Site without logging in, registering, or resetting a password.")
    }

    @Test
    public void testNoIdSiteResultListenerSet() {
        testNoListener(NONE)
    }

    @Test
    public void testNullIdSiteResultListenerSet() {
        testNoListener(SET)
    }

    @Test
    public void testNullIdSiteResultListenerAdd() {
        testNoListener(ADD)
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    public void testSetNonceStore() {
        def dataStore = createStrictMock(InternalDataStore)
        def application = createStrictMock(Application)
        def request = createStrictMock(HttpRequest)
        def cacheResolver = createStrictMock(CacheResolver)

        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3N0dXJkeS1zaGllbGQuaWQuc3Rvcm1w" +
            "YXRoLmlvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hY2NvdW50cy83T3JhOEtmVkRFSVFQMzhLenJZZEFzIi" +
            "wiYXVkIjoiMkVWNzBBSFJUWUYwSk9BN09FRk8zU00yOSIsImV4cCI6MjUwMjQ2NjY1MDAwLCJpYXQiOjE0MDcxOTg1NTAsImp0aSI6" +
            "IjQzNnZra0hnazF4MzA1N3BDUHFUYWgiLCJpcnQiOiIxZDAyZDMzNS1mYmZjLTRlYTgtYjgzNi04NWI5ZTJhNmYyYTAiLCJpc05ld1" +
            "N1YiI6ZmFsc2UsInN0YXR1cyI6IlJFR0lTVEVSRUQifQ.4_yCiF6Cik2wep3iwyinTTcn5GHAEvCbIezO1aA5Kkk"

        expect(request.getMethod()).andReturn(HttpMethod.GET)
        expect(request.getParameter(JWT_RESPONSE)).andReturn(jwtResponse)
        expect(dataStore.getCacheResolver()).andReturn(cacheResolver)

        replay dataStore, application, request, cacheResolver

        def idSiteCallbackHandler = new DefaultIdSiteCallbackHandler(dataStore, application, request)

        def nonceStore = createMock(NonceStore)

        idSiteCallbackHandler.setNonceStore(nonceStore)
        assertEquals idSiteCallbackHandler.nonceStore, nonceStore

        try {
            idSiteCallbackHandler.setNonceStore(null)
        } catch (IllegalArgumentException e) {
            assertEquals e.getMessage(), "[Assertion failed] - this argument is required; it must not be null"
        }
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    public void testGetAccountResultNullHref() {
        def apiKey = ApiKeys.builder()
            .setId('2EV70AHRTYF0JOA7OEFO3SM29')
            .setSecret('goPUHQMkS4dlKwl5wtbNd91I+UrRehCsEDJrIrMruK8')
            .build()
        def apiKeyCredentials = new ApiKeyCredentials(apiKey)
        def requestExecutor = createStrictMock(RequestExecutor)
        def dataStore = new DefaultDataStore(requestExecutor, apiKeyCredentials)
        def application = createStrictMock(Application)
        def request = createStrictMock(HttpRequest)
        def cacheResolver = createStrictMock(CacheResolver)

        // empty subj claim
        // status claim is REGISTERED
        // this is an invalid state
        def jwtResponse = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
            "eyJpc3MiOiJodHRwczovL3N0dXJkeS1zaGllbGQuaWQuc3Rvcm1wYXRoLmlvIiwiaWF0IjoxNDU5MTI0NDQ2LCJleHAiOjE0OTA2Nj" +
            "AxNzUsImF1ZCI6IjJFVjcwQUhSVFlGMEpPQTdPRUZPM1NNMjkiLCJzdWIiOiIiLCJqdGkiOiI0MzZ2a2tIZ2sxeDMwNTdwQ1BxVGFo" +
            "IiwiaXJ0IjoiMWQwMmQzMzUtZmJmYy00ZWE4LWI4MzYtODViOWUyYTZmMmEwIiwiaXNOZXdTdWIiOiJmYWxzZSIsInN0YXR1cyI6Il" +
            "JFR0lTVEVSRUQifQ." +
            "cixfevZzese4V3mUk8K147b2reOJzMlmYIhJev8YbGs"

        expect(request.getMethod()).andReturn(HttpMethod.GET)
        expect(request.getParameter(JWT_RESPONSE)).andReturn(jwtResponse)

        replay requestExecutor, application, request, cacheResolver

        def idSiteCallbackHandler = new DefaultIdSiteCallbackHandler(dataStore, application, request)

        try {
            idSiteCallbackHandler.getAccountResult()
            fail "should not be here"
        } catch (InvalidJwtException e) {
            assertEquals e.getMessage(), InvalidJwtException.JWT_RESPONSE_MISSING_PARAMETER_ERROR
        }

        verify requestExecutor, application, request, cacheResolver
    }

    private void testNoListener(TestIdSiteResultListenerType idSiteResultListenerType) {
        String jwtResponse = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6IjJFVjcwQUhSVFlGMEpPQTdPRUZPM1NNMjkifQ.eyJpc3" +
                "MiOiJodHRwczovL3N0dXJkeS1zaGllbGQuaWQuc3Rvcm1wYXRoLmlvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92" +
                "MS9hY2NvdW50cy83T3JhOEtmVkRFSVFQMzhLenJZZEFzIiwiZXhwIjoyNTAyNDY2NjUwMDAsImlhdCI6MTQwNzE5ODU1MCwianRpIj" +
                "oiNDM2dmtrSGdrMXgzMDU3cENQcVRhaCIsImlydCI6IjFkMDJkMzM1LWZiZmMtNGVhOC1iODM2LTg1YjllMmE2ZjJhMCIsImlzTmV3" +
                "U3ViIjpmYWxzZSwic3RhdHVzIjoiUkVHSVNURVJFRCJ9.0n-Sp5zDtiOIJ_Zq6IYdrDHoU3i95XPKhlH-2n9ALdg"

        def apiKey = ApiKeys.builder().setId('2EV70AHRTYF0JOA7OEFO3SM29').setSecret('goPUHQMkS4dlKwl5wtbNd91I+UrRehCsEDJrIrMruK8').build()
        def apiKeyCredentials = new ApiKeyCredentials(apiKey)
        def requestExecutor = createStrictMock(RequestExecutor)
        def dataStore = new DefaultDataStore(requestExecutor, apiKeyCredentials)
        def application = createStrictMock(Application)
        def request = createStrictMock(HttpRequest)
        def account = createStrictMock(Account)

        expect(request.getMethod()).andReturn(HttpMethod.GET)
        expect(request.getParameter(JWT_RESPONSE)).andReturn(jwtResponse)

        replay application, request, account

        DefaultIdSiteCallbackHandler callbackHandler = new DefaultIdSiteCallbackHandler(dataStore, application, request)

        switch(TestIdSiteResultListenerType) {
            case SET:     callbackHandler.setResultListener(null)
                          break

            case ADD:     callbackHandler.addResultListener(null)
                          break

            case NONE:
                 default: break
        }

        AccountResult accountResult = callbackHandler.getAccountResult()
        assertEquals accountResult.account.href, 'https://api.stormpath.com/v1/accounts/7Ora8KfVDEIQP38KzrYdAs'

        verify application, request, account
    }

    private void testListener(
            String jwtResponse, IdSiteResultStatus expectedListenerMethod, TestIdSiteResultListenerType TestIdSiteResultListenerType
    ) {
        def apiKey = ApiKeys.builder().setId('2EV70AHRTYF0JOA7OEFO3SM29').setSecret('goPUHQMkS4dlKwl5wtbNd91I+UrRehCsEDJrIrMruK8').build()
        def apiKeyCredentials = new ApiKeyCredentials(apiKey)
        def requestExecutor = createStrictMock(RequestExecutor)
        def dataStore = new DefaultDataStore(requestExecutor, apiKeyCredentials)
        def application = createStrictMock(Application)
        def request = createStrictMock(HttpRequest)

        def listener = createStrictMock(IdSiteResultListener)
        def listener2 = createStrictMock(IdSiteResultListener)

        def account = createStrictMock(Account)

        AccountResult accountResultFromListener = null

        expect(request.getMethod()).andReturn(HttpMethod.GET)
        expect(request.getParameter(JWT_RESPONSE)).andReturn(jwtResponse)

        if (expectedListenerMethod.equals(IdSiteResultStatus.REGISTERED)) {
            def listenerDelegate = new IdSiteResultListener() {
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
            }
            expect(listener.onRegistered(anyObject(AccountResult))).andDelegateTo(listenerDelegate)
            if (TestIdSiteResultListenerType == MULTI) {
                expect(listener2.onRegistered(anyObject(AccountResult))).andDelegateTo(listenerDelegate)
            }
        } else if (expectedListenerMethod.equals(IdSiteResultStatus.AUTHENTICATED)) {
            def listenerDelegate = new IdSiteResultListener() {
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
            }
            expect(listener.onAuthenticated(anyObject(AccountResult))).andDelegateTo(listenerDelegate)
            if (TestIdSiteResultListenerType == MULTI) {
                expect(listener2.onAuthenticated(anyObject(AccountResult))).andDelegateTo(listenerDelegate)
            }
        } else if (expectedListenerMethod.equals(IdSiteResultStatus.LOGOUT)) {
            def listenerDelegate = new IdSiteResultListener() {
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
            }
            expect(listener.onLogout(anyObject(AccountResult))).andDelegateTo(listenerDelegate)
            if (TestIdSiteResultListenerType == MULTI) {
                expect(listener2.onLogout(anyObject(AccountResult))).andDelegateTo(listenerDelegate)
            }
        }

        replay application, request, listener, listener2, account

        DefaultIdSiteCallbackHandler callbackHandler = new DefaultIdSiteCallbackHandler(dataStore, application, request)
        callbackHandler.setResultListener(listener)
        if (TestIdSiteResultListenerType == MULTI) {
            callbackHandler.addResultListener(listener2)
        }

        AccountResult accountResult = callbackHandler.getAccountResult()
        assertEquals accountResult.account.href, 'https://api.stormpath.com/v1/accounts/7Ora8KfVDEIQP38KzrYdAs'
        assertEquals accountResultFromListener.account.href, 'https://api.stormpath.com/v1/accounts/7Ora8KfVDEIQP38KzrYdAs'
        assertEquals(accountResult.account, accountResultFromListener.account)
        assertEquals(accountResult.newAccount, accountResultFromListener.newAccount)
        assertEquals(accountResult.state, accountResultFromListener.state)

        verify application, request, listener, account
    }

    /* @since 1.0.RC5 */
    private static void testError(String jwtResponse, int expectedCode, int expectedStatus, String expectedMessage, String expectedDeveloperMessage) {
        def apiKey = ApiKeys.builder().setId('2EV70AHRTYF0JOA7OEFO3SM29').setSecret('goPUHQMkS4dlKwl5wtbNd91I+UrRehCsEDJrIrMruK8').build()
        def apiKeyCredentials = new ApiKeyCredentials(apiKey)

        def requestExecutor = createStrictMock(RequestExecutor)
        def dataStore = new DefaultDataStore(requestExecutor, apiKeyCredentials)
        def application = createStrictMock(Application)
        def request = createStrictMock(HttpRequest)

        expect(request.getMethod()).andReturn(HttpMethod.GET)
        expect(request.getParameter(JWT_RESPONSE)).andReturn(jwtResponse)

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

    /* @since 1.0.RC5 */
    private static void testExpired(String jwtResponse) {
        def apiKey = ApiKeys.builder().setId('2EV70AHRTYF0JOA7OEFO3SM29').setSecret('goPUHQMkS4dlKwl5wtbNd91I+UrRehCsEDJrIrMruK8').build()
        def apiKeyCredentials = new ApiKeyCredentials(apiKey)
        def requestExecutor = createStrictMock(RequestExecutor)
        def dataStore = new DefaultDataStore(requestExecutor, apiKeyCredentials)
        def application = createStrictMock(Application)
        def request = createStrictMock(HttpRequest)

        expect(request.getMethod()).andReturn(HttpMethod.GET)
        expect(request.getParameter(JWT_RESPONSE)).andReturn(jwtResponse)

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

    /* @since 1.0.RC5 */
    private static void testRethrow(String jwtResponse, Class<IDSiteException> expectedExceptionClass, int expectedCode, int expectedStatus, String expectedMessage, String expectedDeveloperMessage) {
        def apiKey = ApiKeys.builder().setId('2EV70AHRTYF0JOA7OEFO3SM29').setSecret('goPUHQMkS4dlKwl5wtbNd91I+UrRehCsEDJrIrMruK8').build()
        def apiKeyCredentials = new ApiKeyCredentials(apiKey)
        def requestExecutor = createStrictMock(RequestExecutor)
        def dataStore = new DefaultDataStore(requestExecutor, apiKeyCredentials)
        def application = createStrictMock(Application)
        def request = createStrictMock(HttpRequest)

        expect(request.getMethod()).andReturn(HttpMethod.GET)
        expect(request.getParameter(JWT_RESPONSE)).andReturn(jwtResponse)

        replay application, request

        DefaultIdSiteCallbackHandler callbackHandler = new DefaultIdSiteCallbackHandler(dataStore, application, request)

        try {
            callbackHandler.getAccountResult()
            throw new Exception("should have thrown")
        } catch (IDSiteRuntimeException ex) {
            try {
                ex.rethrow();
                throw new Exception("should have thrown")
            } catch (InvalidIDSiteTokenException e) {
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
