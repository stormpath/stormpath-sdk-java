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

import com.stormpath.sdk.saml.AttributeStatementMappingRule
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * Test for AttributeStatementMappingRule class
 *
 * @since 1.0.RC8
 */
class DefaultAttributeStatementMappingRuleTest {

    @Test
    void testMethods() {

        AttributeStatementMappingRule rule = new DefaultAttributeStatementMappingRule("testName", "nameFormat", "value1", "value2", "value3")
        assertTrue(rule.getName().equals("testName"))
        assertTrue(rule.getNameFormat().equals("nameFormat"))
        assertTrue(rule.getAccountAttributes() instanceof Set && rule.getAccountAttributes().size() == 3)

        Set<String> values = new HashSet<String>()
        values.add("value1")
        values.add("value2")

        rule = new DefaultAttributeStatementMappingRule("testName1", "nameFormat1", (Set<String>) values)
        assertTrue(rule.getName().equals("testName1"))
        assertTrue(rule.getNameFormat().equals("nameFormat1"))
        assertTrue(rule.getAccountAttributes() instanceof Set && rule.getAccountAttributes().size() == 2)
    }
}
