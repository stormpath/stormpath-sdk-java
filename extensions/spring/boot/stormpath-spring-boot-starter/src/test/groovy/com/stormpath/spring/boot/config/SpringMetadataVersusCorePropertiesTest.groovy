package com.stormpath.spring.boot.config

import com.stormpath.sdk.impl.config.JSONPropertiesSource
import com.stormpath.sdk.impl.config.ResourcePropertiesSource
import com.stormpath.sdk.impl.io.ClasspathResource
import com.stormpath.sdk.impl.io.DefaultResourceFactory
import com.stormpath.sdk.impl.io.Resource
import com.stormpath.sdk.impl.io.ResourceFactory
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * A test that compares the properties in com/stormpath/sdk/config/stormpath.properties
 * with the ones defined in additional-spring-configuration-metadata.json.
 *
 * This test will fail when a new property is added to the the Core SDK's stormpath.properties but
 * it does not exist in additional-spring-configuration-metadata.json.
 *
 * @since 1.0.0
 */
class SpringMetadataVersusCorePropertiesTest {

    def METADATA_CONFIG_LOCATION = ClasspathResource.SCHEME_PREFIX + "META-INF/additional-spring-configuration-metadata.json"
    def DEFAULT_CONFIG_LOCATION = ClasspathResource.SCHEME_PREFIX + "com/stormpath/sdk/config/stormpath.properties"
    def metadataProperties, defaultProperties
    def metadataKeys = []

    @BeforeClass
    void before() {
        ResourceFactory resourceFactory = new DefaultResourceFactory()
        Resource metadataConfig = resourceFactory.createResource(METADATA_CONFIG_LOCATION)
        metadataProperties = new JSONPropertiesSource(metadataConfig).properties
        def names = metadataProperties.findAll({it.key.contains('name')})
        names.each {
            metadataKeys.add(it.value)
        }

        Resource defaultConfig = resourceFactory.createResource(DEFAULT_CONFIG_LOCATION)
        defaultProperties = new ResourcePropertiesSource(defaultConfig).properties
    }

    @Test
    void verifyPropertiesInMetadataAreInDefault() {
        def diff = metadataKeys.findAll {
            defaultProperties.containsKey(it) ? null : it
        }

        assertEquals diff.size(), 7, "Missing keys in default config: ${diff}"
        assertEquals diff.sort().toString(), "[stormpath, stormpath.application, stormpath.application.href, stormpath.client, " +
                "stormpath.client.apiKey.fileIdPropertyName, stormpath.client.apiKey.fileSecretPropertyName, stormpath.enabled]"
    }

    @Test
    void verifyPropertiesInDefaultAreInMetadata() {
        def diff = defaultProperties.findResults { k,v ->
            metadataKeys.contains(k) ? null : k
        }

        assertEquals diff.size(), 0, "Missing keys in metadata config: ${diff}"
    }
}
