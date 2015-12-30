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
package com.stormpath.sdk.impl.saml;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.saml.AttributeStatementMappingRule;
import com.stormpath.sdk.saml.AttributeStatementMappingRules;
import com.stormpath.sdk.saml.AttributeStatementMappingRulesBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @since 1.0.RC8
 */
public class DefaultAttributeStatementMappingRulesBuilder implements AttributeStatementMappingRulesBuilder {

    private Set<AttributeStatementMappingRule> attributeStatementMappingRules;

    public AttributeStatementMappingRulesBuilder setAttributeStatementMappingRules(Set<AttributeStatementMappingRule> attributeStatementMappingRules){
        Assert.notEmpty(attributeStatementMappingRules, "attributeStatementMappingRules argument cannot be null or empty.");

        this.attributeStatementMappingRules = attributeStatementMappingRules;
        return this;
    }

    @Override
    public AttributeStatementMappingRulesBuilder addAttributeStatementMappingRule(AttributeStatementMappingRule attributeStatementMappingRule) {
        Assert.notNull(attributeStatementMappingRule, "attributeStatementMappingRule argument cannot be null or empty.");

        if (this.attributeStatementMappingRules == null){
            this.attributeStatementMappingRules = new LinkedHashSet<AttributeStatementMappingRule>(1);
        } else {
            Assert.isTrue(!attributeStatementMappingRules.contains(attributeStatementMappingRule));
        }

        attributeStatementMappingRules.add(attributeStatementMappingRule);
        return this;
    }

    public AttributeStatementMappingRules build(){
        Assert.notEmpty(attributeStatementMappingRules, "attributeStatementMappingRules argument cannot be null or empty.");

        AttributeStatementMappingRules rules = new DefaultAttributeStatementMappingRules(null);
        rules.setItems(attributeStatementMappingRules);
        return rules;
    }
}
