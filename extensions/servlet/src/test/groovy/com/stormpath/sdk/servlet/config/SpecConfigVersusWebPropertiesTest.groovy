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
package com.stormpath.sdk.servlet.config

import com.stormpath.sdk.impl.config.ResourcePropertiesSource
import com.stormpath.sdk.impl.config.YAMLPropertiesSource
import com.stormpath.sdk.impl.io.ClasspathResource
import com.stormpath.sdk.impl.io.DefaultResourceFactory
import com.stormpath.sdk.impl.io.Resource
import com.stormpath.sdk.impl.io.ResourceFactory
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * A test that downloads the <a href="https://github.com/stormpath/stormpath-framework-spec/blob/master/example-config.yaml">web config
 * from the stormpath-framework-spec</a> and compares it with <code>com/stormpath/sdk/servlet/config/web.stormpath.properties</code>.
 *
 * This test will fail when a new property is added to the spec's example-config.yaml but
 * it does not exist in <code>web.stormpath.properties</code>.
 *
 * @since 1.0.0
 */
class SpecConfigVersusWebPropertiesTest {

    def SPEC_CONFIG_LOCATION = "https://raw.githubusercontent.com/stormpath/stormpath-framework-spec/master/example-config.yaml"
    def DEFAULT_CONFIG_LOCATION = ClasspathResource.SCHEME_PREFIX + "com/stormpath/sdk/servlet/config/web.stormpath.properties"
    def specProperties, defaultProperties

    @BeforeClass
    void before() {
        String specConfig = SPEC_CONFIG_LOCATION.toURL().getText();
        specProperties = new YAMLPropertiesSource(new TestStringResource(specConfig)).properties

        ResourceFactory resourceFactory = new DefaultResourceFactory()
        Resource defaultConfig = resourceFactory.createResource(DEFAULT_CONFIG_LOCATION)
        defaultProperties = new ResourcePropertiesSource(defaultConfig).properties
    }

    /**
     * NOTE: This test is temporarily disabled as 15 new properties have been added to the framework spec for
     * multi-tenancy that are not yet implemented in the SDK.
     * Per high priority ticket: https://github.com/stormpath/stormpath-sdk-java/issues/1033,
     * Todo: this should be re-enabled and support for the new properties should be added asap
     */
    @Test(enabled=false)
    void verifyPropertiesInSpecAreInDefault() {

        def diff = specProperties.findResults { k,v ->
            defaultProperties.containsKey(k) ? null : k
        }

        if (diff.size > 0) {
            println "It looks like a new property was added to the Framework Spec and the Java SDK is missing it."
            println "Missing keys in web.stormpath.properties:"
            diff.each {
                println "${it}"
            }
            println "You must add this new property to the Java SDK in order for this test to pass."
            println "Or you could adjust the assertEquals statement in this method to allow for this missing key as a temporary solution."
        }

        assertEquals 0, diff.size(), "Missing keys in default config: ${diff}"
    }

    @Test
    void verifyWebPropertiesInDefaultAreInSpec() {
        def diff = defaultProperties.findResults { k,v ->
            specProperties.containsKey(k) ? null : k
        }

        def expected_diff_size = 81

        if (diff.size != expected_diff_size) {
            println "It looks like a property was added or removed from the Framework Spec or web.stormpath.properties."
            println "Please examine this method to see the mismatch and commented code for debugging what's changed."
        }

        assertEquals diff.size(), expected_diff_size, "Missing keys in spec config: ${diff}"

        // to see the keys missing in spec, uncomment the following
        /*if (diff.size > 0) {
            println "Missing keys in spec:"
            diff.each {
                println "${it}"
            }
        }*/

        // to see the keys and their values for updating the wiki, uncomment the following
        // https://github.com/stormpath/stormpath-sdk-java/wiki/1.0-Configuration-Changes-&-Additions-Guide#not-in-specification
        /*
        SortedSet<String> keys = new TreeSet<String>(properties.keySet());
        keys.each {
            println("|${it}|" + properties.get(it) + "|")
        }*/
    }

    @Test(enabled = false)
    // todo: Do we care about null vs. blank?
    void verifyValuesMatch() {
        specProperties.findResults { k,v ->
            assertEquals defaultProperties.get(k), v
        }
    }
}

class TestStringResource implements Resource {

    private String string;

    public TestStringResource(String string) {
        this.string = string;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(string.getBytes());
    }
}
