/*
 * Copyright 2017 Stormpath, Inc.
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

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager
import com.stormpath.sdk.servlet.csrf.DefaultCsrfTokenManager
import com.stormpath.sdk.servlet.event.RequestEventListener
import com.stormpath.sdk.servlet.filter.StormpathFilter
import com.stormpath.sdk.servlet.http.authc.HeaderAuthenticator
import com.stormpath.sdk.servlet.mvc.Controller
import com.stormpath.spring.security.provider.GroupPermissionResolver
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * Disabling Spring Security must also disable Stormpath's Spring Security integration
 *
 * @since 1.3.2
 */
@ContextConfiguration(classes = [DisabledSpringSecurityDisablesStormpathSSConfigurationTestAppConfig.class, TwoAppTenantStormpathTestConfiguration.class])
@WebAppConfiguration
class DisabledSpringSecurityDisablesStormpathSSConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    Client client

    @Autowired
    ApiKey apiKey;

    @Autowired
    CacheManager stormpathCacheManager;

    @Autowired
    Application application;

    @Autowired
    StormpathFilter stormpathFilter

    @Autowired(required = false)
    SecurityConfigurerAdapter stormpathSecurityConfigurerAdapter

    @Autowired
    FilterChainProxy springSecurityFilterChain

    @Autowired
    HeaderAuthenticator stormpathAuthorizationHeaderAuthenticator

    @Autowired(required = false)
    Controller stormpathForgotPasswordController

    //Spring Security Beans
    @Autowired(required = false)
    StormpathAuthenticationProvider stormpathAuthenticationProvider

    @Autowired(required = false)
    GroupPermissionResolver stormpathGroupPermissionResolver

    @Autowired
    RequestEventListener stormpathRequestEventListener

    @Autowired
    CsrfTokenManager stormpathCsrfTokenManager

    @Autowired(required = false)
    LogoutHandler stormpathLogoutHandler

    @Test
    void test() {
        assertNotNull client
        assertNotNull apiKey
        assertNotNull stormpathCacheManager
        assertNotNull stormpathFilter
        assertNull stormpathSecurityConfigurerAdapter
        assertNotNull application
        assertNotNull springSecurityFilterChain
        assertNull stormpathAuthenticationProvider
        assertNull stormpathGroupPermissionResolver
        assertNotNull stormpathRequestEventListener
        assertTrue stormpathCsrfTokenManager instanceof DefaultCsrfTokenManager
        assertNotNull stormpathAuthorizationHeaderAuthenticator
        assertNotNull stormpathForgotPasswordController
        assertNull stormpathLogoutHandler //when Spring Security is disabled we do not need our logout handler
    }

}
