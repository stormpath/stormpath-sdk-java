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
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager
import com.stormpath.spring.config.TwoAppTenantStormpathConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 1.0.RC5.2
 */
@SpringApplicationConfiguration(classes = [StormpathWebSecurityAutoConfigurationApplication.class, TwoAppTenantStormpathConfiguration.class])
@WebAppConfiguration
class StormpathWebSecurityAutoConfigurationIT extends AbstractTestNGSpringContextTests {
    @Autowired
    CsrfTokenManager csrfTokenManager

    @Test
    void testCsrfTokenManager() {
        assertEquals csrfTokenManager.tokenName, 'csrfToken'
    }

}
