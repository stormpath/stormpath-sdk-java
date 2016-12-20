package com.stormpath.sdk.provider.social;


import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

import java.util.List;

/**
 * A collection of rules that indicates how a userInfo attribute from a Social Provider should populate one or more
 * Stormpath Account field values.
 * By creating rules, you configure which userInfo attribute values from the Social Provider should be copied to the
 * Stormpath Account field values.
 *
 * <p> Also see {@link UserInfoMappingRule} </p>
 *
 * @since 1.3.0
 */
public interface UserInfoMappingRules extends Resource, Saveable, Auditable {

    /**
     * Specifies the Set of all {@link UserInfoMappingRule}s that indicate how UserInfo attribute should
     * populate one or more Stormpath Account field values after a successful Social (provider) login.
     *
     * @param userInfoMappingRules the set of {@link UserInfoMappingRule userInfoMappingRules}s to build a Social provider.
     *
     * @return this instance for method chaining.
     */
    void setItems(List<UserInfoMappingRule> userInfoMappingRules);

    /**
     * Returns the Set of all {@link UserInfoMappingRule}s that indicate how UserInfo attribute should
     * populate one or more Stormpath Account field values after a successful Social (provider) login.
     *
     * @return the Set of all {@link UserInfoMappingRule}s that indicate how UserInfo attribute should
     * populate one or more Stormpath Account field values after a successful Social (provider) login.
     */
    List<UserInfoMappingRule> getItems();
}
