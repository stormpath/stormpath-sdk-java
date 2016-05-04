package com.stormpath.sdk.impl.config

import com.stormpath.sdk.impl.io.ClasspathResource
import com.stormpath.sdk.impl.io.DefaultResourceFactory
import com.stormpath.sdk.impl.io.Resource
import com.stormpath.sdk.impl.io.ResourceFactory
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * A test that downloads the default config from the spec and compares
 * it with com/stormpath/sdk/config/stormpath.properties
 *
 * @since 1.0.0
 */
class SpecConfigVersusCorePropertiesTest {
    def SPEC_CONFIG_LOCATION = "https://raw.githubusercontent.com/stormpath/stormpath-sdk-spec/master/specifications/config.md"
    def DEFAULT_CONFIG_LOCATION = ClasspathResource.SCHEME_PREFIX + "com/stormpath/sdk/config/stormpath.properties"
    def specProperties, defaultProperties

    @BeforeClass
    void before() {
        String content = SPEC_CONFIG_LOCATION.toURL().getText();
        // find first instance of yaml for default config code block
        String beginToken = "```yaml\n---"
        String endToken = "```"
        int configStart = content.indexOf(beginToken) + beginToken.length()
        String specConfig = content.substring(configStart)
        specConfig = specConfig.substring(0, specConfig.indexOf(endToken))

        specProperties = new YAMLPropertiesSource(new TestStringResource(specConfig)).properties

        ResourceFactory resourceFactory = new DefaultResourceFactory()
        Resource defaultConfig = resourceFactory.createResource(DEFAULT_CONFIG_LOCATION)

        defaultProperties = new ResourcePropertiesSource(defaultConfig).properties
    }

    @Test
    void verifyPropertiesInSpecAreInDefault() {
        def diff = specProperties.findResults { k,v ->
            defaultProperties.containsKey(k) ? null : k
        }

        assertEquals diff.size(), 0, "Missing keys in default config: ${diff}"
    }

    @Test
    void verifyPropertiesInDefaultAreInSpec() {
        def diff = defaultProperties.findResults { k,v ->
            specProperties.containsKey(k) ? null : k
        }

        assertEquals diff.size(), 0, "Missing keys in spec config: ${diff}"
    }

    @Test(enabled = false)
    // todo: Do we care about null vs. blank?
    void verifyValuesMatch() {
        specProperties.findResults { k,v ->
            assertEquals defaultProperties.get(k), v
        }
    }
}
