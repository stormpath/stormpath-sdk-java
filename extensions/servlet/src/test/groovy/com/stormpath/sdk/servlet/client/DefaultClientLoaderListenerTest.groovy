package com.stormpath.sdk.servlet.client

import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigLoader
import com.stormpath.sdk.servlet.utils.ConfigTestUtils
import org.springframework.mock.web.MockServletContext
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.servlet.ServletContextEvent

import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull

/**
 * @since 1.1.0
 */
class DefaultClientLoaderListenerTest {

    MockServletContext mockServletContext
    Config config

    @BeforeMethod
    void setup() {
        mockServletContext = new MockServletContext()
    }

    @Test
    public void testClientLoadedByDefault() {
        config = new ConfigLoader().createConfig(mockServletContext)

        new DefaultClientLoaderListener().contextInitialized(new ServletContextEvent(mockServletContext))
        assertNotNull mockServletContext.getAttribute(ClientLoader.CLIENT_ATTRIBUTE_KEY)
    }

    @Test
    public void testClientLoadingDisabled() {
        Map<String, String> env = new HashMap<>()
        env.put('STORMPATH_ENABLED', 'false')
        ConfigTestUtils.setEnv(env)

        config = new ConfigLoader().createConfig(mockServletContext)

        new DefaultClientLoaderListener().contextInitialized(new ServletContextEvent(mockServletContext))
        assertNull mockServletContext.getAttribute(ClientLoader.CLIENT_ATTRIBUTE_KEY)

        ConfigTestUtils.setEnv(new HashMap())
    }
}
