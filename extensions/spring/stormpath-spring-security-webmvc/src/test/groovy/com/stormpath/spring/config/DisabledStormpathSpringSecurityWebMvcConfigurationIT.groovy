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
import com.stormpath.sdk.cache.CacheManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import javax.servlet.Filter
import static org.testng.Assert.*

/**
 * @since 1.3.0
 */
@ContextConfiguration(classes = [DisabledStormpathSpringSecurityWebMvcTestAppConfig.class, TwoAppTenantStormpathTestConfiguration.class])
@WebAppConfiguration
class DisabledStormpathSpringSecurityWebMvcConfigurationIT extends AbstractClientIT {

    private static final Logger log = LoggerFactory.getLogger(DisabledStormpathSpringSecurityWebMvcConfigurationIT)

    @Autowired
    SecurityConfigurerAdapter stormpathSecurityConfigurerAdapter;

    @Autowired
    ApiKey apiKey;

    @Autowired
    CacheManager stormpathCacheManager;

    @Autowired
    Filter stormpathFilter

    @Autowired
    WebApplicationContext context;

    @Autowired
    protected FilterChainProxy springSecurityFilterChain;

    @Autowired
    protected Filter stormpathFilter;

    private MockMvc mvc;

    @BeforeClass
    public void setUp() {

        super.setUp()

        mvc = MockMvcBuilders.webAppContextSetup(context)
                             .addFilter(springSecurityFilterChain, "/*") //Spring security in front of Stormpath
                             .addFilter(stormpathFilter, "/*")
                             .build();
    }

    @Test
    void testRequiredBeans() {
        assertTrue stormpathSecurityConfigurerAdapter instanceof DisabledStormpathSecurityConfigurerAdapter //let's assert Stormpath's Spring Security integration is disabled
        assertNotNull apiKey
        assertNotNull stormpathCacheManager
        assertNotNull client
        assertNotNull application
        assertNotNull stormpathFilter
    }

}
