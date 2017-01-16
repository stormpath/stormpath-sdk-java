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
import com.stormpath.sdk.servlet.filter.StormpathFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.testng.annotations.Test

import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.3.2
 */
@ContextConfiguration(classes = [DisabledStormpathTestAppConfig.class])
@WebAppConfiguration
class DisabledStormpathConfigurationIT extends AbstractTestNGSpringContextTests {

    private static final Logger log = LoggerFactory.getLogger(DisabledStormpathConfigurationIT)

    @Autowired(required = false)
    Client client

    @Autowired(required = false)
    ApiKey apiKey;

    @Autowired(required = false)
    CacheManager stormpathCacheManager;

    @Autowired(required = false)
    StormpathFilter stormpathFilter

    @Autowired(required = false)
    protected FilterChainProxy springSecurityFilterChain;

    @Autowired(required = false)
    SecurityConfigurerAdapter stormpathSecurityConfigurerAdapter;

    @Autowired(required = false)
    Application application;

    @Test
    void testRequiredBeans() {
        assertNull apiKey
        assertNull stormpathCacheManager
        assertNull client
        assertNull stormpathFilter
        assertNull stormpathSecurityConfigurerAdapter
        assertNull application;
        assertTrue springSecurityFilterChain instanceof FilterChainProxy //let's assert Spring Security's Filter is present
    }

}
