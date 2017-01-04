/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.config.filter

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.impl.client.DefaultClient
import com.stormpath.sdk.servlet.application.ApplicationResolver
import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigLoader
import com.stormpath.sdk.servlet.filter.DefaultFilterConfig
import com.stormpath.sdk.servlet.filter.FilterChainResolver
import com.stormpath.sdk.servlet.filter.WrappedServletRequestFactory
import com.stormpath.sdk.servlet.filter.account.CookieAccountResolver
import com.stormpath.sdk.servlet.http.Resolver
import com.stormpath.sdk.servlet.utils.ConfigTestUtils
import org.springframework.mock.web.MockServletContext
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.4
 */
class SelfConfiguredStormpathFilterTest {

    MockServletContext mockServletContext
    Config config

    @BeforeMethod
    void setup() {
        mockServletContext = new MockServletContext()
    }

    @Test
    public void testStormpathFilterEnabledByDefault() {
        Config config = createMock(Config)
        DefaultClient defaultClient = createMock(DefaultClient)
        ApplicationResolver applicationResolver = createMock(ApplicationResolver)
        FilterChainResolver filterChainResolver = createMock(FilterChainResolver)
        WrappedServletRequestFactory stormpathServletRequestFactoryFactory = createMock(WrappedServletRequestFactory)

        mockServletContext.setAttribute(ConfigLoader.CONFIG_ATTRIBUTE_NAME, config)

        expect(config.isStormpathWebEnabled()).andReturn true
        expect(config.getClient()).andReturn defaultClient
        expect(config.getApplicationResolver()).andReturn applicationResolver
        expect(applicationResolver.getApplication(mockServletContext)).andReturn createMock(Application)
        expect(config.getInstance("stormpath.web.filter.chain.resolver")).andReturn filterChainResolver
        expect(config.get("stormpath.web.request.client.attributeNames")).andReturn "client"
        expect(config.get("stormpath.web.request.application.attributeNames")).andReturn "application"
        expect(config.getInstance("stormpath.web.request.factory")).andReturn stormpathServletRequestFactoryFactory

        replay config, defaultClient, applicationResolver, filterChainResolver, stormpathServletRequestFactoryFactory

        SelfConfiguredStormpathFilter filter = new SelfConfiguredStormpathFilter()
        DefaultFilterConfig filterConfig = new DefaultFilterConfig(mockServletContext, 'filter', null)
        filter.init(filterConfig)
        assertTrue filter.isEnabled()

        verify config, defaultClient, applicationResolver, filterChainResolver, stormpathServletRequestFactoryFactory
    }

    @Test
    public void testCORSFilterIsCreated() {
        Config config = createStrictMock(Config)
        Map accountResolverMap = createStrictMock(Map)
        CookieAccountResolver cookieAccountResolver = createStrictMock(CookieAccountResolver)

        mockServletContext.setAttribute(ConfigLoader.CONFIG_ATTRIBUTE_NAME, config)

        expect(config.getFilterChainManager()).andReturn(null)
        expect(config.getAccessTokenUrl()).andReturn("/oauth/token")
        expect(config.get("stormpath.web.account.resolvers")).andReturn("cookie")
        expect(config.getInstances("stormpath.web.account.resolvers.", Resolver)).andReturn(accountResolverMap)
        expect(accountResolverMap.size()).andReturn(1)
        expect(accountResolverMap.get("cookie")).andReturn(cookieAccountResolver)
        expect(config.isCorsEnabled()).andReturn(true)
        expect(config.getAllowedCorsOrigins()).andReturn(null)
        expect(config.getAllowedCorsHaders()).andReturn(["Content-Type","Accept","X-Requested-With","remember-me"] as List)
        expect(config.getAllowedCorsMethods()).andReturn(["POST","GET","OPTIONS","DELETE"] as List)
        expect(config.isCorsEnabled()).andReturn(true)

        replay config, accountResolverMap, cookieAccountResolver

        FilterChainResolverFactory filterChainResolverFactory = new FilterChainResolverFactory();
        filterChainResolverFactory.init(mockServletContext)

        verify config, accountResolverMap, cookieAccountResolver
    }

    @Test
    public void testStormpathFilterDisabled() {
        Map<String, String> env = new HashMap<>()
        env.put('STORMPATH_WEB_ENABLED', 'false')
        ConfigTestUtils.setEnv(env)

        config = new ConfigLoader().createConfig(mockServletContext)

        SelfConfiguredStormpathFilter filter = new SelfConfiguredStormpathFilter()
        DefaultFilterConfig filterConfig = new DefaultFilterConfig(mockServletContext, 'filter', null)
        filter.init(filterConfig)
        assertFalse filter.isEnabled()

        ConfigTestUtils.setEnv(new HashMap())
    }
}
