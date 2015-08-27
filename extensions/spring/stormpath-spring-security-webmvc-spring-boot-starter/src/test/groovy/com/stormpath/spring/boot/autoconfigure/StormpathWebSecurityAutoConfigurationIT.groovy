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
package com.stormpath.spring.boot.autoconfigure

import autoconfigure.StormpathWebSecurityAutoConfigurationApplication
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.impl.cache.DisabledCacheManager
import com.stormpath.sdk.servlet.config.CookieConfig
import com.stormpath.sdk.servlet.filter.UsernamePasswordRequestFactory
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory
import com.stormpath.sdk.servlet.http.Resolver
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver
import com.stormpath.sdk.servlet.mvc.Controller
import com.stormpath.spring.security.provider.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.HandlerMapping
import org.testng.annotations.Test

import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC4.6
 */
@SpringApplicationConfiguration(classes = StormpathWebSecurityAutoConfigurationApplication.class)
@WebAppConfiguration
class StormpathWebSecurityAutoConfigurationIT extends AbstractTestNGSpringContextTests {

    //Spring Security Bean
    @Autowired
    StormpathAuthenticationProvider stormpathAuthenticationProvider

    //Some WebMVC Beans
    @Autowired
    HandlerMapping stormpathHandlerMapping

    @Autowired
    HandlerInterceptor stormpathLayoutInterceptor

    @Autowired
    AccountStoreResolver stormpathAccountStoreResolver

    @Autowired
    UsernamePasswordRequestFactory stormpathUsernamePasswordRequestFactory

    @Autowired
    CookieConfig stormpathAccountCookieConfig

    @Autowired
    AccessTokenResultFactory stormpathAccessTokenResultFactory

    @Autowired
    Resolver<Account> stormpathCookieAccountResolver

    @Autowired
    Controller stormpathLoginController

    @Autowired
    Resolver<Locale> stormpathLocaleResolver

    @Test
    void test() {

        assertNotNull stormpathAuthenticationProvider
        assertNotNull stormpathAuthenticationProvider.applicationRestUrl
        assertNotNull stormpathAuthenticationProvider.client

        assertTrue stormpathAuthenticationProvider.client.dataStore.cacheManager instanceof DisabledCacheManager
        assertTrue stormpathAuthenticationProvider.groupGrantedAuthorityResolver instanceof DefaultGroupGrantedAuthorityResolver
        assertTrue stormpathAuthenticationProvider.groupPermissionResolver instanceof GroupCustomDataPermissionResolver
        assertTrue stormpathAuthenticationProvider.accountGrantedAuthorityResolver instanceof EmptyAccountGrantedAuthorityResolver
        assertTrue stormpathAuthenticationProvider.accountPermissionResolver instanceof AccountCustomDataPermissionResolver
        assertTrue stormpathAuthenticationProvider.authenticationTokenFactory instanceof UsernamePasswordAuthenticationTokenFactory

        //Some WebMVC beans
        assertNotNull stormpathHandlerMapping
        assertNotNull stormpathLayoutInterceptor
        assertNotNull stormpathAccountStoreResolver
        assertNotNull stormpathUsernamePasswordRequestFactory
        assertNotNull stormpathAccountCookieConfig
        assertNotNull stormpathAccessTokenResultFactory
        assertNotNull stormpathCookieAccountResolver
        assertNotNull stormpathLoginController
        assertNotNull stormpathLocaleResolver
    }

}
