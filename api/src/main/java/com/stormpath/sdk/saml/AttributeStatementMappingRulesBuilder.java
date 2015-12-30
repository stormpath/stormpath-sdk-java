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

import java.util.Set;

/**
 * A Builder to construct {@link AttributeStatementMappingRules} resources.
 * Use example:
 *
 *  AttributeStatementMappingRules attributeStatementMappingRules = SamlEntitiesFactory.getAttributeStatementMappingRulesBuilder()
 *      .addAttributeStatementMappingRule(attributeStatementMappingRule1)
 *      .addAttributeStatementMappingRule(attributeStatementMappingRule2)
 *      ...
 *      .build();
 *
 * @see AttributeStatementMappingRules

 * @since 1.0.RC8
 */
public interface AttributeStatementMappingRulesBuilder {

    /**
     * Specifies the set of {@link AttributeStatementMappingRule AttributeStatementMappingRule}s for the {@link AttributeStatementMappingRules AttributeStatementMappingRules} object,
     * indicating how SAML Attribute Statements should populate one or more Stormpath Account field values after a successful SAML login.
     *
     * @param attributeStatementMappingRules the set of {@link AttributeStatementMappingRule AttributeStatementMappingRule}s for the {@link AttributeStatementMappingRules AttributeStatementMappingRules} object.
     *
     * @return this instance for method chaining.
     */
    AttributeStatementMappingRulesBuilder setAttributeStatementMappingRules(Set<AttributeStatementMappingRule> attributeStatementMappingRules);

    /**
     * Adds a new {@link AttributeStatementMappingRule AttributeStatementMappingRule} to the set of {@link AttributeStatementMappingRule AttributeStatementMappingRule}s,
     * indicating how a SAML Attribute Statement should populate one or more Stormpath Account field values after a successful SAML login.
     *
     * @param attributeStatementMappingRule the {@link AttributeStatementMappingRule AttributeStatementMappingRule} to add to the {@link AttributeStatementMappingRules AttributeStatementMappingRules} object.
     *
     * @return this instance for method chaining.
     */
    AttributeStatementMappingRulesBuilder addAttributeStatementMappingRule(AttributeStatementMappingRule attributeStatementMappingRule);

    /**
     * Builds a new {@link AttributeStatementMappingRules AttributeStatementMappingRules} instance based on the state of this builder.
     *
     * @return a new {@link AttributeStatementMappingRules AttributeStatementMappingRules} instance.
     */
    AttributeStatementMappingRules build();
}
