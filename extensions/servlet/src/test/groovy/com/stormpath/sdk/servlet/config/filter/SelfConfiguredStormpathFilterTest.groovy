package com.stormpath.sdk.servlet.config.filter

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.impl.client.DefaultClient
import com.stormpath.sdk.servlet.application.ApplicationResolver
import com.stormpath.sdk.servlet.application.DefaultApplicationResolver
import com.stormpath.sdk.servlet.cache.PropertiesCacheManagerFactory
import com.stormpath.sdk.servlet.client.ClientLoader
import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigLoader
import com.stormpath.sdk.servlet.filter.DefaultFilterConfig
import com.stormpath.sdk.servlet.filter.FilterChainResolver
import com.stormpath.sdk.servlet.filter.WrappedServletRequestFactory
import com.stormpath.sdk.servlet.filter.config.StormpathServletRequestFactoryFactory
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
