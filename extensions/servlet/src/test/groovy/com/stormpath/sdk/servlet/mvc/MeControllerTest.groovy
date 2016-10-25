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

import com.fasterxml.jackson.databind.ObjectMapper
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.impl.account.DefaultAccount
import com.stormpath.sdk.impl.resource.Property
import com.stormpath.sdk.servlet.application.ApplicationResolver
import com.stormpath.sdk.servlet.filter.LoginPageRedirector
import com.stormpath.sdk.servlet.http.MediaType
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*

/**
 * @since 1.2.0
 */
public class MeControllerTest {

    @Test
    public void testMeExpandOptionsEvaluatedPerRequest(){
        ObjectMapper objectMapper = createNiceMock(ObjectMapper)
        List<MediaType> stormpathProducedMediaTypes = new ArrayList<>()
        stormpathProducedMediaTypes.add(MediaType.APPLICATION_JSON)
        String meUri = "/me"
        LoginPageRedirector loginPageRedirector = createNiceMock(LoginPageRedirector)
        ApplicationResolver applicationResolver = createNiceMock(ApplicationResolver)

        DefaultAccount account = createNiceMock(DefaultAccount)
        expect(account.getPropertyDescriptors()).andReturn(new HashMap<String, Property>()).anyTimes()

        HttpServletRequest request = createNiceMock(HttpServletRequest)
        expect(request.getAttribute(Account.class.getName())).andReturn(account).times(2)

        HttpServletResponse response = createNiceMock(HttpServletResponse)

        List<String> expands = new ArrayList<>()
        expands.add("tenant")
        expands.add("directory")

        /*Expands Resolver should be called upon to generate expands list for each request to MeController*/
        ExpandsResolver expandsResolver = createStrictMock(ExpandsResolver)
        expect(expandsResolver.getExpands()).andReturn(expands).times(2)

        MeController meController = new MeController()
        meController.setExpandsResolver(expandsResolver);
        meController.setObjectMapper(objectMapper);
        meController.setProduces(stormpathProducedMediaTypes);
        meController.setUri(meUri);
        meController.setLoginPageRedirector(loginPageRedirector);
        meController.setApplicationResolver(applicationResolver);

        replay(objectMapper, loginPageRedirector, applicationResolver, expandsResolver, request, response, account)

        meController.init()
        meController.doGet(request, response)
        meController.doPost(request, response)

        verify(objectMapper, loginPageRedirector, applicationResolver, expandsResolver, request, response, account)
    }

}
