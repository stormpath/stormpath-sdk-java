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

import com.stormpath.sdk.lang.Classes;

/**
 * Static utility/helper methods for working with SAML-related resources. Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for
 * creating {@link AttributeStatementMappingRule} and {@link AttributeStatementMappingRules} entities.
 *
 * @since 1.0.RC8
 */
public final class SamlAttributeStatementMappingRules {

    /**
     * Returns a new {@link AttributeStatementMappingRuleBuilder} instance, used to construct {@link AttributeStatementMappingRule} objects,
     * to be added to the {@link com.stormpath.sdk.saml.AttributeStatementMappingRules#getItems() getItems()} collection of {@link AttributeStatementMappingRules} class.
     *
     * @return a new {@link AttributeStatementMappingRuleBuilder} instance, used to construct {@link AttributeStatementMappingRule} objects.
     */
    public static AttributeStatementMappingRuleBuilder ruleBuilder() {
        return (AttributeStatementMappingRuleBuilder) Classes.newInstance("com.stormpath.sdk.impl.saml.DefaultAttributeStatementMappingRuleBuilder");
    }

    /**
     * Returns a new {@link AttributeStatementMappingRulesBuilder} instance, used to construct {@link AttributeStatementMappingRules},
     * to be added to the {@link com.stormpath.sdk.provider.saml.SamlProvider} entity, for example, when creating a new Saml Directory.
     *
     * @return a new {@link AttributeStatementMappingRuleBuilder} instance, used to construct {@link AttributeStatementMappingRules}.
     */
    public static AttributeStatementMappingRulesBuilder rulesBuilder() {
        return (AttributeStatementMappingRulesBuilder) Classes.newInstance("com.stormpath.sdk.impl.saml.DefaultAttributeStatementMappingRulesBuilder");
    }
}
