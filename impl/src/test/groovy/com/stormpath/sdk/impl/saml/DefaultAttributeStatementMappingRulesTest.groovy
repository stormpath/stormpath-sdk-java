/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.saml

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.SetProperty
import com.stormpath.sdk.saml.AttributeStatementMappingRule
import com.stormpath.sdk.saml.AttributeStatementMappingRules
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*


/**
 * Test for AttributeStatementMappingRules class
 *
 * @since 1.0.RC8
 */
class DefaultAttributeStatementMappingRulesTest {

    @Test
    void testGetPropertyDescriptors() {

        AttributeStatementMappingRules attributeStatementMappingRules = new DefaultAttributeStatementMappingRules(createStrictMock(InternalDataStore))

        def propertyDescriptors = attributeStatementMappingRules.getPropertyDescriptors()
        assertEquals(propertyDescriptors.size(), 1)

        assertTrue(propertyDescriptors.get("items") instanceof SetProperty && propertyDescriptors.get("items").getType().equals(AttributeStatementMappingRule.class))
    }

    @Test
    void testMethods() {

        def internalDataStore = createStrictMock(InternalDataStore)

        Set<String> itemsSet = new HashSet<String>(Arrays.asList("this set has five items".split(" ")));

        def properties = [href: "https://api.stormpath.com/v1/directories/45YM3OwioW9PVtfLOh6q1e/saml/sso/post",
            items: itemsSet]

        AttributeStatementMappingRules mappingRules = new DefaultAttributeStatementMappingRules(internalDataStore, properties)

        replay internalDataStore

        Set<AttributeStatementMappingRule> items = new HashSet<AttributeStatementMappingRule>()
        items.add(new DefaultAttributeStatementMappingRule("name", "nameFormat", "value1", "value2"))
        items.add(new DefaultAttributeStatementMappingRule("name1", "nameFormat1", "value3", "value4"))
        mappingRules.setItems()

        assertTrue(mappingRules.getHref().equals(properties.href))
        assertTrue(mappingRules.getItems().size() == 5)

        verify internalDataStore
    }
}
