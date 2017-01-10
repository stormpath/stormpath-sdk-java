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

/**
 * A Builder to construct {@link UserInfoMappingRulesBuilder} resources.
 * Use example:
 *
 *  UserInfoMappingRulesBuilder userInfoMappingRulesBuilder = UserInfoMappingRulesBuilder.rulesBuilder()
 *      .addUserInfoMappingRule(userInfoMappingRule1)
 *      .addUserInfoMappingRule(userInfoMappingRule2)
 *      ...
 *      .build();
 *
 * @see UserInfoMappingRulesBuilder

 * @since 1.3.0
 */
public interface UserInfoMappingRulesBuilder {
    /**
     * Adds a new {@link UserInfoMappingRule UserInfoMappingRule} to the set of {@link UserInfoMappingRule UserInfoMappingRule}s,
     * indicating how a fields from the userInfo provided by the social provider should populate one or more Stormpath Account
     * field values after a successful Social login.
     *
     * @param userInfoMappingRule the {@link UserInfoMappingRule UserInfoMappingRule} to add to the {@link UserInfoMappingRules UserInfoMappingRules} object.
     *
     * @return this instance for method chaining.
     */
    UserInfoMappingRulesBuilder addUserInfoMappingRule(UserInfoMappingRule userInfoMappingRule);

    /**
     * Builds a new {@link UserInfoMappingRules UserInfoMappingRules} instance based on the state of this builder.
     *
     * @return a new {@link UserInfoMappingRules UserInfoMappingRules} instance.
     */
    UserInfoMappingRules build();
}
