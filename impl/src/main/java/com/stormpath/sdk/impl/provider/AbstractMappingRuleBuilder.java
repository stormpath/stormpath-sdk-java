package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.MappingRule;
import com.stormpath.sdk.provider.MappingRuleBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @since 1.3.0
 */
public abstract class AbstractMappingRuleBuilder implements MappingRuleBuilder {

    protected String name;
    protected Set<String> accountAttributes;

    @Override
    public MappingRuleBuilder setName(String name) {
        Assert.hasText(name, "name argument cannot be null or empty.");
        this.name = name;
        return this;
    }

    @Override
    public MappingRuleBuilder setAccountAttributes(String... accountAttributes) {
        Assert.notEmpty(accountAttributes, "accountAttributes cannot be null or empty.");

        Set<String> names = new LinkedHashSet<String>(accountAttributes.length);
        for (String attrName : accountAttributes) {
            Assert.hasText("individual accountAttributes cannot be null or empty.");
            names.add(attrName);
        }
        this.accountAttributes = names;
        return this;
    }

    @Override
    public MappingRuleBuilder setAccountAttributes(Set<String> accountAttributes) {
        Assert.notEmpty(accountAttributes, "accountAttributes cannot be null or empty.");

        this.accountAttributes = accountAttributes;
        return this;
    }

    public abstract MappingRule build();
}
