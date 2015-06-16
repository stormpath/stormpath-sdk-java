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

import autoconfigure.TestBootSpringSecurityApplication
import com.stormpath.sdk.impl.cache.DisabledCacheManager
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.RC4.4
 */
@SpringApplicationConfiguration(classes = TestBootSpringSecurityApplication.class)
@Configuration
class StormpathSpringSecurityConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    StormpathAuthenticationProvider stormpathAuthenticationProvider;

    @Test
    void test() {

        assertNotNull stormpathAuthenticationProvider
        assertEquals stormpathAuthenticationProvider.applicationRestUrl, "https://api.stormpath.com/v1/applications/3TqbyZ1qo74eDM4gTo2H94"
        assertNotNull stormpathAuthenticationProvider.client

        assertTrue stormpathAuthenticationProvider.client.dataStore.cacheManager instanceof DisabledCacheManager
        assertTrue stormpathAuthenticationProvider.groupGrantedAuthorityResolver instanceof CustomTestGroupGrantedAuthorityResolver
    }

}
