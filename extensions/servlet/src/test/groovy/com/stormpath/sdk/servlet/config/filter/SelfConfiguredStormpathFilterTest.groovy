package com.stormpath.sdk.servlet.config.filter

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.impl.client.DefaultClient
import com.stormpath.sdk.servlet.application.DefaultApplicationResolver
import com.stormpath.sdk.servlet.cache.PropertiesCacheManagerFactory
import com.stormpath.sdk.servlet.client.ClientLoader
import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigLoader
import com.stormpath.sdk.servlet.filter.DefaultFilterConfig
import com.stormpath.sdk.servlet.http.impl.StormpathHttpServletRequest
import com.stormpath.sdk.servlet.utils.ConfigTestUtils
import org.springframework.mock.web.MockServletContext
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify
import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.isA
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
        config = new ConfigLoader().createConfig(mockServletContext)

        // make SelfConfiguredStormpathFilter think a client has already been initialized
        DefaultClient defaultClient = createMock(DefaultClient)
        expect(defaultClient.getResource("http://app", Application.class)).andReturn(createMock(Application))

        // create cacheManagerFactory
        PropertiesCacheManagerFactory factory = new PropertiesCacheManagerFactory();
        CacheManager cacheManager = factory.createCacheManager(config);

        ApiKey apiKey = createMock(ApiKey)
        expect(apiKey.getSecret()).andReturn("secret")
        expect(defaultClient.getCacheManager()).andReturn(cacheManager)
        expect(defaultClient.getApiKey()).andReturn(apiKey)

        mockServletContext.setAttribute(ClientLoader.CLIENT_ATTRIBUTE_KEY, defaultClient);
        mockServletContext.setAttribute(DefaultApplicationResolver.STORMPATH_APPLICATION_HREF, "http://app")

        replay apiKey, defaultClient

        SelfConfiguredStormpathFilter filter = new SelfConfiguredStormpathFilter()
        DefaultFilterConfig filterConfig = new DefaultFilterConfig(mockServletContext, 'filter', null)
        filter.init(filterConfig)
        assertTrue filter.isEnabled()

        verify apiKey, defaultClient
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

    @Test
    public void testPatternsToIgnore() {
        config = new ConfigLoader().createConfig(mockServletContext)

        // make SelfConfiguredStormpathFilter think a client has already been initialized
        DefaultClient defaultClient = createMock(DefaultClient)
        expect(defaultClient.getResource("http://app", Application.class)).andReturn(createMock(Application))

        // create cacheManagerFactory
        PropertiesCacheManagerFactory factory = new PropertiesCacheManagerFactory();
        CacheManager cacheManager = factory.createCacheManager(config);

        ApiKey apiKey = createMock(ApiKey)
        expect(apiKey.getSecret()).andReturn("secret")
        expect(defaultClient.getCacheManager()).andReturn(cacheManager)
        expect(defaultClient.getApiKey()).andReturn(apiKey)

        mockServletContext.setAttribute(ClientLoader.CLIENT_ATTRIBUTE_KEY, defaultClient);
        mockServletContext.setAttribute(DefaultApplicationResolver.STORMPATH_APPLICATION_HREF, "http://app")

        // verify default is to filter JS and CSS files
        HttpServletRequest request = createStrictMock(HttpServletRequest)
        HttpServletResponse response = createStrictMock(HttpServletResponse)
        FilterChain filterChain = createStrictMock(FilterChain)

        expect(request.getRequestURL()).andReturn(new StringBuffer("/path/to/file.js"))
        expect(filterChain.doFilter(request, response));

        replay apiKey, defaultClient, filterChain, request, response

        SelfConfiguredStormpathFilter filter = new SelfConfiguredStormpathFilter()

        filter.setPathsToIgnore("([^\\s]+(\\.(?i)(js|map|json|css|png|gif|jpg|jpeg|woff2|html))\$)")

        DefaultFilterConfig filterConfig = new DefaultFilterConfig(mockServletContext, 'filter', null)
        filter.init(filterConfig)
        assertTrue filter.isEnabled()

        filter.filter(request, response, filterChain)

        verify apiKey, defaultClient, filterChain, request, response
    }

    @Test
    public void verifyMeEndpointIsNotIgnored() {
        config = new ConfigLoader().createConfig(mockServletContext)

        // make SelfConfiguredStormpathFilter think a client has already been initialized
        DefaultClient defaultClient = createMock(DefaultClient)
        Application application = createMock(Application);
        expect(defaultClient.getResource("http://app", Application.class)).andReturn(application)

        // create cacheManagerFactory
        PropertiesCacheManagerFactory factory = new PropertiesCacheManagerFactory();
        CacheManager cacheManager = factory.createCacheManager(config);

        ApiKey apiKey = createMock(ApiKey)
        expect(apiKey.getSecret()).andReturn("secret")
        expect(defaultClient.getCacheManager()).andReturn(cacheManager)
        expect(defaultClient.getApiKey()).andReturn(apiKey)

        mockServletContext.setAttribute(ClientLoader.CLIENT_ATTRIBUTE_KEY, defaultClient);
        mockServletContext.setAttribute(DefaultApplicationResolver.STORMPATH_APPLICATION_HREF, "http://app")

        // verify default is to filter JS and CSS files
        HttpServletRequest request = createStrictMock(HttpServletRequest)
        HttpServletResponse response = createStrictMock(HttpServletResponse)
        FilterChain filterChain = createStrictMock(FilterChain)

        expect(request.getRequestURL()).andReturn(new StringBuffer("/me"))
        expect(request.setAttribute(Client.class.getName(), defaultClient))
        expect(request.setAttribute("client", defaultClient))
        expect(request.setAttribute(Application.class.getName(), application))
        expect(request.setAttribute("application", application))
        expect(request.getHeaderNames()).andReturn(Collections.emptyEnumeration())
        expect(request.getAttribute("javax.servlet.include.context_path")).andReturn("/")
        expect(request.getCharacterEncoding()).andReturn("UTF-8")
        expect(request.getAttribute("javax.servlet.include.request_uri")).andReturn("/me")
        expect(request.getCharacterEncoding()).andReturn("UTF-8")
        expect(request.getAttribute("accountResolverFilter.FILTERED")).andReturn(Boolean.TRUE)
        expect(filterChain.doFilter(isA(StormpathHttpServletRequest), isA(HttpServletResponse)));

        replay apiKey, defaultClient, filterChain, request, response

        SelfConfiguredStormpathFilter filter = new SelfConfiguredStormpathFilter()

        filter.setPathsToIgnore("([^\\s]+(\\.(?i)(js|map|json|css|png|gif|jpg|jpeg|woff2|html))\$)")

        DefaultFilterConfig filterConfig = new DefaultFilterConfig(mockServletContext, 'filter', null)
        filter.init(filterConfig)
        assertTrue filter.isEnabled()

        filter.filter(request, response, filterChain)

        verify apiKey, defaultClient, filterChain, request, response
    }
}
