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

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 1.0.RC9
 */
class OptionalPropertiesSourceTest {

    @Test
    void testGetPropertiesSuccess() {

        def properties = new OptionalPropertiesSource(new PropertiesSource() {
            @Override
            Map<String, String> getProperties() {
                return ["my_special_key":"my_special_value"]
            }
        })

        assertEquals properties.getProperties().get("my_special_key"), "my_special_value"
    }

    @Test
    void testGetPropertiesFail() {

        def properties = new OptionalPropertiesSource(new PropertiesSource() {
            @Override
            Map<String, String> getProperties() {
                throw new Exception("Fell down, go boom")
            }
        })

        def result = properties.getProperties()
        assertEquals result.size(), 0
    }
}
