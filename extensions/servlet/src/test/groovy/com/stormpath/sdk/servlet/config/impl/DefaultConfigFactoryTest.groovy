package com.stormpath.sdk.servlet.config.impl

import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigLoader
import org.springframework.mock.web.MockServletContext
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 1.0.RC9
 */
class DefaultConfigFactoryTest {

    MockServletContext mockServletContext

    @BeforeMethod
    void setup() {
        mockServletContext = new MockServletContext();
    }

    @Test
    public void testReadFromDefaultProperties() {
        Config config = new ConfigLoader().createConfig(mockServletContext)

        assertEquals config.get('stormpath.web.login.uri'), '/login'
    }

    @Test
    public void testStormPathPropertiesInClasspathOverridesDefault() {
        Config config = new ConfigLoader().createConfig(mockServletContext)

        assertEquals config.get('stormpath.web.login.nextUri'), '/foo'
    }

    @Test
    public void testStormPathYamlInClasspathOverridesDefault() {
        Config config = new ConfigLoader().createConfig(mockServletContext)

        assertEquals config.get('stormpath.web.logout.uri'), '/getout'
    }
}
