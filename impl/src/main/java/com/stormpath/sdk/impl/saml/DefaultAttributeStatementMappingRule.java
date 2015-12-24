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
    private final Set<String> accountAttributeNames;

    public DefaultAttributeStatementMappingRule(String name, Set<String> accountAttributeNames) {
        Assert.hasText(name, "name argument cannot be null or empty.");
        Assert.notEmpty(accountAttributeNames, "accountAttributeNames cannot be null or empty.");
        this.name = name;
        this.accountAttributeNames = Collections.unmodifiableSet(accountAttributeNames);
    }

    public DefaultAttributeStatementMappingRule(String name, String... accountAttributeNames) {
        Assert.hasText(name, "name argument cannot be null or empty.");
        Assert.notNull(accountAttributeNames, "accountAttributeNames cannot be null or empty.");
        Set<String> names = new LinkedHashSet<String>(accountAttributeNames.length);
        for (String attrName : accountAttributeNames) {
            Assert.hasText("individual accountAttributeNames cannot be null or empty.");
            names.add(attrName);
        }
        this.name = name;
        Assert.notEmpty(names, "accountAttributeNames cannot be null or empty.");
        this.accountAttributeNames = Collections.unmodifiableSet(names);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getAccountAttributeNames() {
        return this.accountAttributeNames;
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
