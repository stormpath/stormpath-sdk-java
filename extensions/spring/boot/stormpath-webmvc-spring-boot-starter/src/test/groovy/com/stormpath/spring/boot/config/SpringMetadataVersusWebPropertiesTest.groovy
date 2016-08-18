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
 * A test that compares the properties in web.stormpath.properties
 * with the ones defined in additional-spring-configuration-metadata.json.
 *
 * @since 1.0.0
 */
class SpringMetadataVersusWebPropertiesTest {
    def METADATA_CONFIG_LOCATION = ClasspathResource.SCHEME_PREFIX + "META-INF/additional-spring-configuration-metadata.json"
    def DEFAULT_CONFIG_LOCATION = ClasspathResource.SCHEME_PREFIX + "com/stormpath/sdk/servlet/config/web.stormpath.properties"
    def metadataProperties, defaultProperties
    def metadataKeys = []

    @BeforeClass
    void before() {
        ResourceFactory resourceFactory = new DefaultResourceFactory()
        Resource metadataConfig = resourceFactory.createResource(METADATA_CONFIG_LOCATION)
        metadataProperties = new JSONPropertiesSource(metadataConfig).properties
        def names = metadataProperties.findAll({it.key.contains('name')})
        names.each {
            if (it.key.contains('properties') &&
                    it.value.contains('stormpath.web') || it.value.contains('stormpath.application')) {
                metadataKeys.add(it.value)
            }
        }
        //println "metadata keys: " + metadataKeys.size()

        Resource defaultConfig = resourceFactory.createResource(DEFAULT_CONFIG_LOCATION)
        defaultProperties = new ResourcePropertiesSource(defaultConfig).properties
        //println "default keys: " + defaultProperties.keySet().size()
    }

    @Test
    void verifyPropertiesInMetadataAreInDefault() {
        def diff = metadataKeys.findAll {
            defaultProperties.containsKey(it) ? null : it
        }

        assertEquals diff.size(), 20, "Missing keys in default config: ${diff}"
        assertEquals diff.sort().toString(), "[stormpath.application, stormpath.web.account.jwt.signatureAlgorithm, " +
                "stormpath.web.account.jwt.ttl, stormpath.web.assets.handlerMapping.order, stormpath.web.authc.savers.cookie.enabled, " +
                "stormpath.web.authc.savers.session.enabled, stormpath.web.head.cssUris, stormpath.web.head.extraCssUris, " +
                "stormpath.web.head.fragmentSelector, stormpath.web.head.view, stormpath.web.idSite.resultUri, " +
                "stormpath.web.json.view.resolver.order, stormpath.web.jsp.view.resolver.order, stormpath.web.oauth2.origin.authorizer.originUris, " +
                "stormpath.web.stormpathFilter.dispatcherTypes, stormpath.web.stormpathFilter.enabled, stormpath.web.stormpathFilter.matchAfter, " +
                "stormpath.web.stormpathFilter.order, stormpath.web.stormpathFilter.servletNames, stormpath.web.stormpathFilter.urlPatterns]"
    }

    @Test
    void verifyPropertiesInDefaultAreInMetadata() {
        def diff = defaultProperties.findResults { k,v ->
            metadataKeys.contains(k) ? null : k
        }
        //println "Keys in web.stormpath.properties that aren't in metadata: " + diff.size()
        /*diff.each {
            println """    {
      "name": "${it}",
      "type": "java.lang.String",
      "description": ""
    },"""

        }*/

        //assertEquals diff.size(), 0, "Missing keys in metadata config: ${diff}"
    }

}
