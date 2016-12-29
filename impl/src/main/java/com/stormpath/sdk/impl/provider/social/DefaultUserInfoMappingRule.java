/*
* Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.impl.provider.social;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.social.UserInfoMappingRule;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @since 1.3.0
 */
public class DefaultUserInfoMappingRule implements UserInfoMappingRule {

    private final String name;
    private final Set<String> accountAttributes;

    public DefaultUserInfoMappingRule(String name, Set<String> accountAttributes) {
        Assert.hasText(name, "name argument cannot be null or empty.");
        Assert.notEmpty(accountAttributes, "accountAttributes cannot be null or empty.");

        this.name = name;
        this.accountAttributes = Collections.unmodifiableSet(accountAttributes);
    }

    public DefaultUserInfoMappingRule(String name, String... accountAttributes) {
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
    }

    @Override
    public String getName() {
        return this.name;
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
        if (o instanceof DefaultUserInfoMappingRule) {
            DefaultUserInfoMappingRule rule = (DefaultUserInfoMappingRule) o;
            return name.equals(rule.getName());
        }
        return false;
    }
}
