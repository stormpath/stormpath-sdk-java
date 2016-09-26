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
package com.stormpath.sdk.impl.config

import com.stormpath.sdk.impl.io.ClasspathResource
import com.stormpath.sdk.impl.io.DefaultResourceFactory
import com.stormpath.sdk.impl.io.Resource
import com.stormpath.sdk.impl.io.ResourceFactory
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * A test that downloads the <a href="https://github.com/stormpath/stormpath-sdk-spec/blob/master/specifications/config.md">default
 * config from the stormpath-sdk-spec</a> and compares it with <code>com/stormpath/sdk/config/stormpath.properties</code>.
 *
 * This test will fail when a new property is added to the the spec's default config but
 * it does not exist in <code>stormpath.properties</code>.
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

        if (diff.size > 0) {
            println "It looks like a new property was added to the SDK Spec and the Java SDK is missing it."
            println "Missing keys in stormpath.properties:"
            diff.each {
                println "${it}"
            }
            println "You must add this new property to the Java SDK in order for this test to pass."
            println "Or you could adjust the assertEquals statement in this method to allow for this missing key as a temporary solution."
        }

        assertEquals diff.size(), 0, "Missing keys in default config: ${diff}"
    }

    @Test
    void verifyPropertiesInDefaultAreInSpec() {
        def diff = defaultProperties.findResults { k,v ->
            specProperties.containsKey(k) ? null : k
        }

        if (diff.size > 0) {
            println "It looks like a new property was added to stormpath.properties, but the SDK Spec is missing it."
            println "Missing keys in SDK Spec:"
            diff.each {
                println "${it}"
            }
            println "You must add this new property to the SDK Spec in order for this test to pass."
            println "Or you could adjust the assertEquals statement in this method to allow for this missing key as a temporary solution."
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
