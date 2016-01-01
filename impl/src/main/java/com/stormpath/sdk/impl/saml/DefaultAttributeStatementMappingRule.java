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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @since 1.0.RC8
 */
public class DefaultAttributeStatementMappingRule implements AttributeStatementMappingRule {

    private final String name;
    private final String nameFormat;
    private final Set<String> accountAttributes;

    public DefaultAttributeStatementMappingRule(String name, String nameFormat, Set<String> accountAttributes) {
        Assert.hasText(name, "name argument cannot be null or empty.");
        Assert.notEmpty(accountAttributes, "accountAttributes cannot be null or empty.");

        this.name = name;
        this.accountAttributes = Collections.unmodifiableSet(accountAttributes);
        this.nameFormat = nameFormat != null ? nameFormat : null;
    }

    public DefaultAttributeStatementMappingRule(String name, String nameFormat, String... accountAttributes) {
        Assert.hasText(name, "name argument cannot be null or empty.");
        Assert.notNull(accountAttributes, "accountAttributes cannot be null or empty.");

        Set<String> names = new LinkedHashSet<String>(accountAttributes.length);
        for (String attrName : accountAttributes) {
            Assert.hasText("individual accountAttributes cannot be null or empty.");
            names.add(attrName);
        }
        this.name = name;
        Assert.notEmpty(names, "accountAttributes cannot be null or empty.");
        this.accountAttributes = Collections.unmodifiableSet(names);
        this.nameFormat = nameFormat != null ? nameFormat : null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getNameFormat() {
        return this.nameFormat;
    }

    @Override
    public Set<String> getAccountAttributes() {
        return this.accountAttributes;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof DefaultAttributeStatementMappingRule) {
            DefaultAttributeStatementMappingRule rule = (DefaultAttributeStatementMappingRule) o;
            return name.equals(rule.getName());
        }
        return false;
    }
}
