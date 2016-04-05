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

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.Client
import com.stormpath.spring.config.TwoAppTenantStormpathTestConfiguration
import com.stormpath.spring.security.provider.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.RC5
 */
@SpringApplicationConfiguration(classes = [SpringSecurityBootTestApplication.class, TwoAppTenantStormpathTestConfiguration.class])
class StormpathSpringSecurityAutoConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    Application application

    @Autowired
    Client client

    @Autowired
    StormpathAuthenticationProvider authenticationProvider;

    @Autowired
    GroupGrantedAuthorityResolver groupGrantedAuthorityResolver

    @Autowired
    GroupPermissionResolver groupPermissionResolver

    @Autowired
    AccountGrantedAuthorityResolver accountGrantedAuthorityResolver

    @Autowired
    AccountPermissionResolver accountPermissionResolver

    @Autowired
    AuthenticationTokenFactory authenticationTokenFactory

    @Test
    void test() {

        assertNotNull authenticationProvider
        assertEquals authenticationProvider.application, application

        assertTrue groupGrantedAuthorityResolver instanceof DefaultGroupGrantedAuthorityResolver
        assertTrue groupPermissionResolver instanceof GroupCustomDataPermissionResolver
        assertTrue accountGrantedAuthorityResolver instanceof EmptyAccountGrantedAuthorityResolver
        assertTrue accountPermissionResolver instanceof AccountCustomDataPermissionResolver
        assertTrue authenticationTokenFactory instanceof UsernamePasswordAuthenticationTokenFactory

        assertSame authenticationProvider.groupGrantedAuthorityResolver, groupGrantedAuthorityResolver
        assertSame authenticationProvider.groupPermissionResolver, groupPermissionResolver
        assertSame authenticationProvider.accountGrantedAuthorityResolver, accountGrantedAuthorityResolver
        assertSame authenticationProvider.accountPermissionResolver, accountPermissionResolver
        assertSame authenticationProvider.authenticationTokenFactory, authenticationTokenFactory
    }

}
