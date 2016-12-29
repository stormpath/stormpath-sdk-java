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
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.servlet.Filter

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated

import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertTrue

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;

/**
 * Validates that https://github.com/stormpath/stormpath-sdk-java/issues/1158 is fixed
 *
 * @since 1.3.0
 */
@WebAppConfiguration
@ContextConfiguration(classes = [LoginAndLogoutHandlersConfig.class, TwoAppTenantStormpathTestConfiguration.class])
class LoginAndLogoutHandlersIT extends AbstractClientIT {

    @Autowired
    WebApplicationContext context;

    @Autowired
    @Qualifier("stormpathAuthenticationSuccessHandler")
    public AuthenticationSuccessHandler customLoginSuccessHandler;

    @Autowired
    @Qualifier("stormpathAuthenticationFailureHandler")
    public AuthenticationFailureHandler customLoginFailureHandler;

    @Autowired
    @Qualifier("stormpathLogoutHandler")
    public LogoutHandler customLogoutHandler;

    @Autowired
    protected FilterChainProxy springSecurityFilterChain;

    @Autowired
    protected Filter stormpathFilter;

    private MockMvc mvc;

    @BeforeMethod
    public void setup() {
          mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(StormpathMockMvcConfigurers.stormpath())
                .build()
    }

    @Test
    void testHandlersAreInvoked() {

        assertFalse customLoginSuccessHandler.invoked
        assertFalse customLoginFailureHandler.invoked
        assertFalse customLogoutHandler.invoked

        def password = "P@\$sw0rd*"
        def account = createTempAccount(password)

        mvc.perform(SecurityMockMvcLoginRequestBuilder.formLogin().login(account.getEmail()).password("this_password_will_fail")).andExpect(unauthenticated())

        assertFalse customLoginSuccessHandler.invoked
        assertTrue customLoginFailureHandler.invoked //was the login failure handler actually invoked?
        assertFalse customLogoutHandler.invoked

        mvc.perform(SecurityMockMvcLoginRequestBuilder.formLogin().login(account.getEmail()).password(password)).andExpect(authenticated())

        assertTrue customLoginSuccessHandler.invoked //was the login success handler actually invoked?

        mvc.perform(logout())

        assertTrue customLogoutHandler.invoked //was the logout handler actually invoked?
    }


}
