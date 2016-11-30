package com.stormpath.sdk.provider.social;


import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

import java.util.List;

public interface UserInfoMappingRules extends Resource, Saveable, Auditable {

    /**
     * Specifies the Set of all {@link UserInfoMappingRule}s that indicate how SAML Attribute Statements should
     * populate one or more Stormpath Account field values after a successful SAML login.
     *
     * @param userInfoMappingRules the set of {@link UserInfoMappingRule userInfoMappingRules}s to build a Social provider.
     *
     * @return this instance for method chaining.
     * @since 1.3.0
     */
    void setItems(List<UserInfoMappingRule> userInfoMappingRules);

    /**
     * Returns the Set of all {@link UserInfoMappingRule}s that indicate how SAML Attribute Statements should
     * populate one or more Stormpath Account field values after a successful SAML login.
     *
     * @return the Set of all {@link UserInfoMappingRule}s that indicate how SAML Attribute Statements should
     * populate one or more Stormpath Account field values after a successful SAML login.
     * @since 1.3.0
     */
    List<UserInfoMappingRule> getItems();
}
