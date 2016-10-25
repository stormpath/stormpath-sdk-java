/*
 * Copyright 2016 Stormpath, Inc.
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

package com.stormpath.sdk.servlet.mvc

import com.stormpath.sdk.servlet.authz.RequestAuthorizer
import com.stormpath.sdk.servlet.event.impl.Publisher
import com.stormpath.sdk.servlet.filter.oauth.*
import com.stormpath.sdk.servlet.http.Saver
import com.stormpath.sdk.servlet.util.GrantTypeStatusValidator
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*

/**
 * @since 1.2.0
 */
class AccessTokenControllerTest {


    @Test
    public void testGrantTypeValidationFailuresHandled() {

        RefreshTokenAuthenticationRequestFactory refreshTokenAuthenticationRequestFactory = createNiceMock(RefreshTokenAuthenticationRequestFactory)
        RefreshTokenResultFactory refreshTokenResultFactory = createNiceMock(RefreshTokenResultFactory)
        RequestAuthorizer requestAuthorizer = createNiceMock(RequestAuthorizer)
        AccessTokenAuthenticationRequestFactory accessTokenAuthenticationRequestFactory = createNiceMock(AccessTokenAuthenticationRequestFactory)
        AccessTokenResultFactory accessTokenResultFactory = createNiceMock(AccessTokenResultFactory)
        Publisher eventPublisher = createNiceMock(Publisher)
        Saver saver = createNiceMock(Saver)

        AccessTokenController controller = new AccessTokenController()

        controller.setRefreshTokenAuthenticationRequestFactory(refreshTokenAuthenticationRequestFactory)
        controller.setRefreshTokenResultFactory(refreshTokenResultFactory)
        controller.setRequestAuthorizer(requestAuthorizer)
        controller.setAccessTokenAuthenticationRequestFactory(accessTokenAuthenticationRequestFactory)
        controller.setAccessTokenResultFactory(accessTokenResultFactory)
        controller.setEventPublisher(eventPublisher)
        controller.setAccountSaver(saver)

        GrantTypeStatusValidator grantTypeStatusValidator = createStrictMock(GrantTypeStatusValidator)
        expect(grantTypeStatusValidator.validate(anyString())).andThrow(new OAuthException(OAuthErrorCode.UNSUPPORTED_GRANT_TYPE)).once()
        controller.setGrantTypeStatusValidator(grantTypeStatusValidator)

        controller.init();

        HttpServletRequest request = createNiceMock(HttpServletRequest)
        expect(request.getParameter("grant_type")).andReturn("client_credentials")

        PrintWriter printWriter = createNiceMock(PrintWriter)

        HttpServletResponse response = createNiceMock(HttpServletResponse)
        expect(response.getWriter()).andReturn(printWriter).times(2)

        expect(response.setStatus(HttpServletResponse.SC_BAD_REQUEST))
        expect(printWriter.print("{\"error\":\"unsupported_grant_type\"}"))

        replay(refreshTokenAuthenticationRequestFactory, refreshTokenResultFactory, requestAuthorizer, accessTokenAuthenticationRequestFactory, accessTokenResultFactory, eventPublisher, saver, grantTypeStatusValidator, request, response, printWriter)

        controller.doPost(request, response)

        verify(refreshTokenAuthenticationRequestFactory, refreshTokenResultFactory, requestAuthorizer, accessTokenAuthenticationRequestFactory, accessTokenResultFactory, eventPublisher, saver, grantTypeStatusValidator, request, response, printWriter)
    }

}
