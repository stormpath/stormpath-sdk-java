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
package com.stormpath.sdk.servlet

import com.stormpath.sdk.impl.config.ResourcePropertiesSource
import com.stormpath.sdk.impl.io.ClasspathResource
import com.stormpath.sdk.impl.io.DefaultResourceFactory
import com.stormpath.sdk.impl.io.Resource
import com.stormpath.sdk.impl.io.ResourceFactory
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * A test that compares the Stormpath error codes to make sure they're translated in
 * <code>com/stormpath/sdk/servlet/i18n.properties</code>.
 *
 * This test will fail when a new error code is added but it does not exist in
 * <code>i18n.properties</code>. Of course, <code>api-errors.properties</code> will
 * need to be updated when the backend adds new error codes.
 *
 * @since 1.0.0
 */
class ErrorCodesVersusi18nPropertiesTest {

    def ERRORS_LOCATION = ClasspathResource.SCHEME_PREFIX + "com/stormpath/sdk/servlet/api-errors.properties"
    def I18N_LOCATION = ClasspathResource.SCHEME_PREFIX + "com/stormpath/sdk/servlet/i18n.properties"
    def errorsProperties, i18nProperties

    @BeforeClass
    void before() {
        ResourceFactory resourceFactory = new DefaultResourceFactory()
        Resource resource = resourceFactory.createResource(I18N_LOCATION)
        i18nProperties = new ResourcePropertiesSource(resource).properties
        resource = resourceFactory.createResource(ERRORS_LOCATION)
        errorsProperties = new ResourcePropertiesSource(resource).properties
    }

    @Test
    void verifyErrorCodesAreTranslated() {
        def diff = errorsProperties.findResults { k,v ->
            // chop of 'status' prefix in errors
            def keyPrefix = k.substring(0, k.indexOf('.'))
            // we're only interested in status.* keys
            if (keyPrefix.contains('status')) {
                // ignore keys with additional keys (after error code)
                def errorCode = k.substring(k.indexOf(keyPrefix) + keyPrefix.length() + 1)
                if (!errorCode.contains('.')) {
                    i18nProperties.containsKey('stormpath.web.errors.' + errorCode) ? null : errorCode
                }
            }
        }

        if (diff.size > 0) {
            println "An error code exists in api-errors.properties that hasn't been translated in i18n.properties."
            println "Missing errors in i18n.properties:"
            diff.each {
                println "${it}"
            }
            println "You must add these error codes to i18n.properties for this test to pass."
        }

        assertEquals diff.size(), 0, "Missing error codes in i18n.properties: ${diff}"
    }
}