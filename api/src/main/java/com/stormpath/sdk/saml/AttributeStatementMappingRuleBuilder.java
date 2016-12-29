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
package com.stormpath.sdk.saml;

import com.stormpath.sdk.provider.MappingRuleBuilder;

/**
 * A Builder to construct {@link AttributeStatementMappingRule} resources.
 * Usage Example:
 *
 *  AttributeStatementMappingRule rule = SamlAttributeStatementMappingRules.ruleBuilder()
 *      .setName("name")
 *      .setAccountAttributes("field1", "field2")
 *      .build();
 *
 * @see AttributeStatementMappingRule
 *
 * @since 1.0.RC8
 */
public interface AttributeStatementMappingRuleBuilder extends MappingRuleBuilder {

    /**
     * Sets the format for the SAML Attribute specified by {@link com.stormpath.sdk.saml.AttributeStatementMappingRule#getName() getName()}.
     * Examples of valid formats are:
     *  urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress
     *  urn:oasis:names:tc:SAML:2.0:nameid-format:persistent
     *  urn:oasis:names:tc:SAML:2.0:nameid-format:transient
     *  urn:oasis:names:tc:SAML:2.0:attrname-format:basic
     *  urn:oasis:names:tc:SAML:2.0:nameid-format:entity
     *
     * @param nameFormat the format for the SAML Attribute specified by {@link com.stormpath.sdk.saml.AttributeStatementMappingRule#getName() getName()}.
     */
    AttributeStatementMappingRuleBuilder setNameFormat(String nameFormat);

}
