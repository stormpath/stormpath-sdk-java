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
import com.stormpath.spring.context.MessageSourceDefinitionPostProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.testng.annotations.Test

import javax.servlet.Filter

import static org.testng.Assert.assertNotNull

/**
 * @since 1.0.RC5
 */
@ContextConfiguration(classes = [MinimalWebMvcTestAppConfig.class, TwoAppTenantStormpathTestConfiguration.class])
@WebAppConfiguration
class MinimalStormpathWebMvcConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    StormpathWebMvcConfiguration c;

    @Autowired
    ApiKey apiKey;

    @Autowired
    CacheManager stormpathCacheManager;

    @Autowired
    Client client;

    @Autowired
    Application application;

    @Autowired
    Filter stormpathFilter;

    @Autowired
    MessageSource messageSource;

    @Test
    void test() {
        assertNotNull c
        assertNotNull apiKey
        assertNotNull stormpathCacheManager
        assertNotNull client
        assertNotNull application
        assertNotNull stormpathFilter

        assert messageSource instanceof ResourceBundleMessageSource
        assert messageSource.basenameSet.size() == 1
        assert messageSource.basenameSet.iterator().next() == MessageSourceDefinitionPostProcessor.I18N_PROPERTIES_BASENAME
    }
}
