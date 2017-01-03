package com.stormpath.sdk.provider;

import com.stormpath.sdk.provider.social.UserInfoMappingRules;
import com.stormpath.sdk.saml.SamlAttributeStatementMappingRules;

import java.util.Set;

/**
 * @since 1.3.0
 */
public interface MappingRuleBuilder {

    /**
     * Sets the attribute name, that when encountered, should have its value applied as Account field values.
     * When this name is encountered when processing UserInfo from a Social Provider or SAML Attribute Statement,
     * its associated value will be set as the value for all Stormpath Account field names specified in the
     * {@link MappingRule#getAccountAttributes() accountAttributes} collection.
     *
     * @param name the attribute name that when encountered, should have its value applied as Account field values.
     */
    MappingRuleBuilder setName(String name);

    /**
     * Sets the Stormpath account fields that should be updated when encountering {@link MappingRule#getName() named}
     * field from the userInfo provided by the social provider or SAML Attribute Statement.
     * If discovered, that attribute value will be set on
     * all of the Stormpath account fields named in this collection.
     *
     * @param accountAttributes the account fields that should be updated when there's a match with a field name in userInfo
     * provided by the social provider or SAML Attribute Statement.
     */
    MappingRuleBuilder setAccountAttributes(String... accountAttributes);

    /**
     * Sets the Stormpath account fields that should be updated when encountering {@link MappingRule#getName() named}
     * field from the userInfo provided by the social provider.  If discovered, that attribute value will be set on
     * all of the Stormpath account fields named in this collection.
     *
     * @param accountAttributes the account fields that should be updated when there's a match with a field name in userInfo
     * provided by the social provider or SAML Attribute Statement.
     */
    MappingRuleBuilder setAccountAttributes(Set<String> accountAttributes);

    /**
     * Builds a new {@link MappingRule} based on the current state of this builder.
     *
     * @return a new {@link MappingRule} to be included in the {@link UserInfoMappingRules} for a Social Provider or
     * the {@link SamlAttributeStatementMappingRules} SAML Provider.
     */
    MappingRule build();
}
