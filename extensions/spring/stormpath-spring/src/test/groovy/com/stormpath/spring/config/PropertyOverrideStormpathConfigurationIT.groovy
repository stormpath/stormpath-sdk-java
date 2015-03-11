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
package com.stormpath.spring.config

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.impl.cache.DisabledCacheManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.testng.Assert.*

@ContextConfiguration(classes = PropertyOverrideAppConfig.class)
class PropertyOverrideStormpathConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private StormpathConfiguration c;

    @Autowired
    ApiKey apiKey;

    @Autowired
    CacheManager stormpathCacheManager;

    @Autowired
    Client client;

    @Autowired
    Application application;

    @Test
    void test() {
        assertNotNull c
        assertNotNull apiKey
        assertNotNull stormpathCacheManager
        assertNotNull client
        assertNotNull application

        //PropertyOverrideAppConfig.properties turns caching off, ensure it is reflected:
        assertFalse c.cachingEnabled
        assertTrue stormpathCacheManager instanceof DisabledCacheManager

        //assert app href override worked as expected:
        assertEquals application.href, 'https://api.stormpath.com/v1/applications/295wLwTPf2T3zOEXosMvHy'
    }
}
