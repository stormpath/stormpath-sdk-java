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
package com.stormpath.sdk.provider.social;

import com.stormpath.sdk.lang.Classes;

/**
 * Static utility/helper methods for working with userInfo from Social Provider related resources.
 * Most methods are <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for
 * creating {@link UserInfoMappingRule} and {@link UserInfoMappingRules} entities.
 *
 * @since 1.3.0
 */
public final class SocialUserInfoMappingRules {

    /**
     * Returns a new {@link UserInfoMappingRuleBuilder} instance, used to construct {@link UserInfoMappingRule} objects,
     * to be added to the {@link UserInfoMappingRules#getItems() getItems()} collection of {@link UserInfoMappingRules} class.
     *
     * @return a new {@link UserInfoMappingRuleBuilder} instance, used to construct {@link UserInfoMappingRule} objects.
     */
    public static UserInfoMappingRuleBuilder ruleBuilder() {
        return (UserInfoMappingRuleBuilder) Classes.newInstance("com.stormpath.sdk.impl.provider.social.DefaultUserInfoMappingRuleBuilder");
    }

    /**
     * Returns a new {@link UserInfoMappingRulesBuilder} instance, used to construct {@link UserInfoMappingRules},
     * to be added to the {@link com.stormpath.sdk.provider.OAuthProvider} entity, for example, when creating a new Social Directory.
     *
     * @return a new {@link UserInfoMappingRulesBuilder} instance, used to construct {@link UserInfoMappingRules}.
     */
    public static UserInfoMappingRulesBuilder rulesBuilder() {
        return (UserInfoMappingRulesBuilder) Classes.newInstance("com.stormpath.sdk.impl.provider.social.DefaultUserInfoMappingRulesBuilder");
    }
}
