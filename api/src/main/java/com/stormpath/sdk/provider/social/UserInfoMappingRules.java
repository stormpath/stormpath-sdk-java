package com.stormpath.sdk.provider.social;


import com.stormpath.sdk.resource.Resource;

import java.util.Set;

public interface UserInfoMappingRules extends Resource, Set<UserInfoMappingRule> {

    /**
     * Removes the {@link UserInfoMappingRule}(s) identified by {@code ruleNames}.
     *
     * @param ruleNames the name of the {@link UserInfoMappingRule}(s) to remove.
     *
     * @return  the {@code Set<AttributeStatementMappingRule>} after the "remove" operation is performed.
     */
    Set<UserInfoMappingRule> removeByName(String... ruleNames);

    /**
     * Specifies the Set of all {@link UserInfoMappingRule}s that indicate how SAML Attribute Statements should
     * populate one or more Stormpath Account field values after a successful SAML login.
     *
     * @param attributeStatementMappingRules the set of {@link UserInfoMappingRule AttributeStatementMappingRule}s to build a SAML provider.
     *
     * @return this instance for method chaining.
     */
    void setItems(Set<UserInfoMappingRule> attributeStatementMappingRules);

    /**
     * Returns the Set of all {@link UserInfoMappingRule}s that indicate how SAML Attribute Statements should
     * populate one or more Stormpath Account field values after a successful SAML login.
     *
     * @return the Set of all {@link UserInfoMappingRule}s that indicate how SAML Attribute Statements should
     * populate one or more Stormpath Account field values after a successful SAML login.
     */
    Set<UserInfoMappingRule> getItems();
}
