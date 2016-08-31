package com.stormpath.sdk.servlet.application

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.impl.client.DefaultClient
import com.stormpath.sdk.servlet.client.ClientLoader
import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigLoader
import com.stormpath.sdk.servlet.utils.ConfigTestUtils
import org.springframework.mock.web.MockServletContext
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.servlet.ServletContextEvent

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull

/**
 * @since 1.1.0
 */
class DefaultApplicationLoaderListenerTest {

    MockServletContext mockServletContext
    Config config

    @BeforeMethod
    void setup() {
        mockServletContext = new MockServletContext()
    }

    @Test
    public void testApplicationLoadedByDefault() {
        config = new ConfigLoader().createConfig(mockServletContext)

        // make ApplicationLoaderListener think a client has already been initialized
        DefaultClient defaultClient = createMock(DefaultClient)
        expect(defaultClient.getResource("http://app", Application.class)).andReturn(createMock(Application))
        mockServletContext.setAttribute(ClientLoader.CLIENT_ATTRIBUTE_KEY, defaultClient);
        mockServletContext.setAttribute(DefaultApplicationResolver.STORMPATH_APPLICATION_HREF, "http://app")

        replay defaultClient

        new DefaultApplicationLoaderListener().contextInitialized(new ServletContextEvent(mockServletContext))
        assertNotNull mockServletContext.getAttribute(ApplicationLoader.APP_ATTRIBUTE_NAME)

        verify defaultClient
    }

    @Test
    public void testApplicationLoadingDisabled() {
        Map<String, String> env = new HashMap<>()
        env.put('STORMPATH_ENABLED', 'false')
        ConfigTestUtils.setEnv(env)

        config = new ConfigLoader().createConfig(mockServletContext)

        new DefaultApplicationLoaderListener().contextInitialized(new ServletContextEvent(mockServletContext))
        assertNull mockServletContext.getAttribute(ApplicationLoader.APP_ATTRIBUTE_NAME)

        ConfigTestUtils.setEnv(new HashMap())
    }
}
