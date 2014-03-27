/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.client

import com.stormpath.sdk.client.ApiKeys
import com.stormpath.sdk.impl.util.StringInputStream
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 *
 * @since 1.0.alpha
 */
class DefaultApiKeyBuilderTest {

    @Test
    void testSetApiKey() {
        def apiKeyId = "fooId"
        def apiKeySecret = "barSecret"
        def builder = ApiKeys.builder().setApiKey(apiKeyId, apiKeySecret)
        assertEquals(builder.apiKey.getId(), apiKeyId)
        assertEquals(builder.apiKey.getSecret(), apiKeySecret)
    }

    @Test
    void testApiKeyProperties() {
        def apiKeyIdPropertyName = "myPropertyId"
        def apiKeySecretPropertyName = "myPropertySecret"
        def apiKeyId = "fooId"
        def apiKeySecret = "barSecret"
        def properties = new Properties()

        properties.setProperty(apiKeyIdPropertyName, apiKeyId)
        properties.setProperty(apiKeySecretPropertyName, apiKeySecret)

        def builder = ApiKeys.builder()
                .setApiKeyIdPropertyName(apiKeyIdPropertyName)
                .setApiKeySecretPropertyName(apiKeySecretPropertyName)
                .setApiKeyProperties(properties)

        assertEquals(builder.apiKeyProperties.get(apiKeyIdPropertyName), apiKeyId)
        assertEquals(builder.apiKeyProperties.get(apiKeySecretPropertyName), apiKeySecret)

        def apiKey = builder.build()

        assertEquals(apiKey.getId(), apiKeyId)
        assertEquals(apiKey.getSecret(), apiKeySecret)
    }

    @Test
    void testApiKeyReader() {

        def apiKeyIdPropertyName = "apiKey.id"
        def apiKeySecretPropertyName = "apiKey.secret"

        def apiKeyId = "fooId"
        def apiKeySecret = "barSecret"

        def reader = new StringReader(apiKeyIdPropertyName + "=" + apiKeyId + "\n" +
                apiKeySecretPropertyName + "=" + apiKeySecret)

        def builder = ApiKeys.builder().setApiKeyReader(reader)

        def apiKey = builder.build()

        assertEquals(apiKey.getId(), apiKeyId)
        assertEquals(apiKey.getSecret(), apiKeySecret)
    }

    @Test
    void testApiKeyReaderNull() {

        def builder = ApiKeys.builder().setApiKeyReader(null)

        try {
            builder.build()
            fail("Should have thrown because of null Reader.")
        } catch (IllegalArgumentException e) {
            //Expected exception
        }
    }

    @Test
    void testEmptyPropertyValue() {

        def apiKeyIdPropertyName = "apiKey.id"
        def apiKeySecretPropertyName = "apiKey.secret"

        def apiKeyId = ""
        def apiKeySecret = "barSecret"

        def is = new StringInputStream(apiKeyIdPropertyName + "=" + apiKeyId + "\n" +
                apiKeySecretPropertyName + "=" + apiKeySecret)


        try {
            ApiKeys.builder()
                    .setApiKeyInputStream(is)
                    .build()
            fail("Should have thrown due to empty apiKey.id value.")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "There is no 'apiKey.id' property in the configured apiKey properties.  " +
                    "You can either specify that property or configure the apiKeyIdPropertyName value on the ClientBuilder " +
                    "to specify a custom property name.")
        }

    }

    @Test
    void testInvalidApiKeyFileLocation() {
        try {
            ApiKeys.builder()
                    .setApiKeyFileLocation("/tmp/someUnexistentApiKeyPropertiesFile.properties")
                    .setApiKeyInputStream(null)
                    .build()
            fail("Should have thrown due to invalid ApiKeyFileLocation.")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Unable to load API Key using apiKeyFileLocation '/tmp/someUnexistentApiKeyPropertiesFile.properties'.  " +
                    "Please check and ensure that file exists or use the 'setApiKeyFileLocation' method to specify a valid location.")
        }
    }

    @Test
    void testEmptyBuilder() {
        try {
            ApiKeys.builder().build()
            fail("Should have thrown due to invalid builder state.")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "No API Key properties could be found or loaded from a file location.  " +
                    "Please configure the 'apiKeyFileLocation' property or alternatively configure a Properties, Reader or InputStream instance.")
        }
    }

}
