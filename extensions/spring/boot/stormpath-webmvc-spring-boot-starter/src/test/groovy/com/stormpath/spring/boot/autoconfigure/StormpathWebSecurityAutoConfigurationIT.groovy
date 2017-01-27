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

import autoconfigure.StormpathWebSecurityAutoConfigurationTestApplication
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager
import com.stormpath.sdk.servlet.filter.StormpathFilter
import com.stormpath.sdk.servlet.mvc.Controller
import com.stormpath.sdk.servlet.mvc.IdSiteLoginController
import com.stormpath.sdk.servlet.mvc.LoginController
import com.stormpath.spring.config.TwoAppTenantStormpathTestConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC5.2
 */
@SpringBootTest(classes = [StormpathWebSecurityAutoConfigurationTestApplication.class, TwoAppTenantStormpathTestConfiguration.class])
@WebAppConfiguration
@TestPropertySource(locations = "classpath:it.application.properties")
class StormpathWebSecurityAutoConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    CsrfTokenManager csrfTokenManager

    @Autowired
    @Qualifier("stormpathLoginController")
    Controller login

    @Autowired
    @Qualifier("stormpathRegisterController")
    Controller register

    @Autowired
    @Qualifier("stormpathIdSiteResultController")
    Controller idSiteResultController

    @Value("#{ @environment['stormpath.web.idSite.enabled'] ?: false }")
    protected boolean idSiteEnabled;

    @Test
    void testCsrfTokenManager() {
        assertEquals csrfTokenManager.tokenName, 'csrfToken'
    }

    //asserts https://github.com/stormpath/stormpath-sdk-java/issues/1242
    @Test
    void assertPrePostHandlersArePresentWhenIDSiteIsEnabled() {
        assertTrue idSiteEnabled
        assertTrue(register.preRegisterHandler instanceof StormpathWebSecurityAutoConfigurationTestApplication.CustomRegisterPreHandler)
        assertTrue(idSiteResultController.postRegisterHandler instanceof StormpathWebSecurityAutoConfigurationTestApplication.CustomRegisterPostHandler)
        assertTrue(login.preLoginHandler instanceof StormpathWebSecurityAutoConfigurationTestApplication.CustomLoginPreHandler)
        assertTrue(idSiteResultController.postLoginHandler instanceof StormpathWebSecurityAutoConfigurationTestApplication.CustomLoginPostHandler)
    }
}
