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
import com.stormpath.sdk.impl.cache.DefaultCacheManager
import com.stormpath.sdk.lang.Duration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static org.testng.Assert.*

/**
 * @since 1.0.RC5
 */
@ContextConfiguration(classes = [MinimalTestAppConfig.class, TwoAppTenantStormpathTestConfiguration.class])
class MinimalStormpathConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    StormpathConfiguration c;

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

        assertNull c.baseUrl
        assertNull c.apiKeyFile
        assertNull c.apiKeyFileIdPropertyName
        assertNull c.apiKeyFileSecretPropertyName
        assertNull c.applicationHref
        assertTrue c.cachingEnabled
        assertNull c.proxyHost
        assertEquals c.proxyPort, 80
        assertNull c.proxyUsername
        assertNull c.proxyPassword
        assertEquals c.connectionTimeout, 0
        assertNull c.authenticationScheme

        assertEquals client.dataStore.apiKey, apiKey
        assertEquals client.dataStore.cacheManager, stormpathCacheManager
        assertTrue stormpathCacheManager instanceof DefaultCacheManager

        Duration oneHour = new Duration(1, TimeUnit.HOURS)
        assertEquals stormpathCacheManager.defaultTimeToLive, oneHour
        assertEquals stormpathCacheManager.defaultTimeToIdle, oneHour
    }
}
