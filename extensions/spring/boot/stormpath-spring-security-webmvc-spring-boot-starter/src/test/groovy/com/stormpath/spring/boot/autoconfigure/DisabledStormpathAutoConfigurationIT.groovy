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
package com.stormpath.spring.boot.autoconfigure

import autoconfigure.DisabledStormpathAutoConfigurationTestApplication
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager
import com.stormpath.sdk.servlet.event.RequestEventListener
import com.stormpath.sdk.servlet.filter.StormpathFilter
import com.stormpath.sdk.servlet.http.authc.HeaderAuthenticator
import com.stormpath.sdk.servlet.mvc.Controller
import com.stormpath.spring.security.provider.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.testng.annotations.Test

import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.5.0
 */
@SpringBootTest(classes = [DisabledStormpathAutoConfigurationTestApplication.class])
@WebAppConfiguration
class DisabledStormpathAutoConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired(required = false)
    Client client

    @Autowired(required = false)
    ApiKey apiKey;

    @Autowired(required = false)
    CacheManager stormpathCacheManager;

    @Autowired(required = false)
    Application application;

    @Autowired(required = false)
    StormpathFilter stormpathFilter

    @Autowired(required = false)
    SecurityConfigurerAdapter stormpathSecurityConfigurerAdapter;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    @Autowired(required = false)
    HeaderAuthenticator stormpathAuthorizationHeaderAuthenticator

    @Autowired(required = false)
    Controller stormpathForgotPasswordController

    //Spring Security Beans
    @Autowired(required = false)
    StormpathAuthenticationProvider stormpathAuthenticationProvider

    @Autowired(required = false)
    GroupPermissionResolver stormpathGroupPermissionResolver

    @Autowired(required = false)
    RequestEventListener stormpathRequestEventListener

    @Autowired(required = false)
    CsrfTokenManager stormpathCsrfTokenManager

    @Test
    void test() {
        assertNull client
        assertNull apiKey
        assertNull stormpathCacheManager
        assertNull stormpathFilter
        assertNull stormpathSecurityConfigurerAdapter
        assertNull application;
        assertTrue springSecurityFilterChain instanceof FilterChainProxy //let's assert Spring Security's Filter is present
        assertNull stormpathAuthenticationProvider
        assertNull stormpathGroupPermissionResolver
        assertNull stormpathRequestEventListener
        assertNull stormpathCsrfTokenManager
        assertNull stormpathAuthorizationHeaderAuthenticator
        assertNull stormpathForgotPasswordController
    }

}
