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
package com.stormpath.spring.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertTrue;

/**
 * @since 1.0.RC9
 */
@WebAppConfiguration
@ContextConfiguration(classes = [CustomRequestEventListenerConfig.class, TwoAppTenantStormpathTestConfiguration.class])
class CustomRequestEventListenerIT extends AbstractTestNGSpringContextTests {

    @Autowired
    WebApplicationContext context;

    @Autowired
    AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    CustomRequestEventListenerConfig.CustomRequestEventListener listener;

    private MockMvc mvc;

    @BeforeMethod
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testCustomRequestEventListenerCalled() {

        assertFalse listener.failedInvoked

        mvc.perform(formLogin().password("invalid")).andExpect(unauthenticated())

        assertTrue listener.failedInvoked
    }
}
