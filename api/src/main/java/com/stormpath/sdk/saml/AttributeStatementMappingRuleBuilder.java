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
 * A Builder to construct {@link AttributeStatementMappingRule} resources.
 * Usage Example:
 *
 *  AttributeStatementMappingRule rule = SamlEntitiesFactory.getAttributeStatementMappingRuleBuilder()
 *      .setName("name")
 *      .setAccountAttributeNames("field1", "field2")
 *      .build();
 *
 * @see AttributeStatementMappingRule
 *
 * @since 1.0.RC8
 */
public interface AttributeStatementMappingRuleBuilder {

    /**
     * Sets the SAML Attribute name, that when encountered, should have its value applied as Account field values.
     * When this name is encountered when processing a SAML Attribute Statement, its associated value will be set as the
     * value for all Stormpath Account field names specified in the
     * {@link AttributeStatementMappingRule#getAccountAttributeNames() accountAttributeNames} collection.
     *
     * @param name the SAML Attribute name that when encountered, should have its value applied as Account field values.
     */
    AttributeStatementMappingRuleBuilder setName(String name);

    /**
     * Sets the Stormpath account fields that should be updated when encountering {@link AttributeStatementMappingRule#getName() named}
     * SAML Attribute name.  If discovered, that SAML Attribute value will be set on all of the Stormpath account
     * fields named in this collection.
     *
     * @param accountAttributeNames the account fields that should be updated when there's a match with SAML Attribute name.
     */
    AttributeStatementMappingRuleBuilder setAccountAttributeNames(String... accountAttributeNames);

    /**
     * Sets the Stormpath account fields that should be updated when encountering {@link AttributeStatementMappingRule#getName() named}
     * SAML Attribute name.  If discovered, that SAML Attribute value will be set on all of the Stormpath account
     * fields named in this collection.
     *
     * @param accountAttributeNames the account fields that should be updated when there's a match with SAML Attribute name.
     */
    AttributeStatementMappingRuleBuilder setAccountAttributeNames(Set<String> accountAttributeNames);

    /**
     * Builds a new {@link AttributeStatementMappingRule} based on the current state of this builder.
     *
     * @return a new {@link AttributeStatementMappingRule} to be included in the {@link AttributeStatementMappingRules} for a Saml Provider.
     */
    AttributeStatementMappingRule build();
}
