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
package com.stormpath.sdk.provider.social;

import java.util.Set;

/**
 * A Builder to construct {@link UserInfoMappingRule} resources.
 * Usage Example:
 *
 *  UserInfoMappingRule rule = SocialUserInfoMappingRule.ruleBuilder()
 *      .setName("name")
 *      .setAccountAttributes("field1", "field2")
 *      .build();
 *
 * @see UserInfoMappingRule
 *
 * @since 1.3.0
 */
public interface UserInfoMappingRuleBuilder {

    /**
     * Sets the UserInfo attribute name, that when encountered, should have its value applied as Account field values.
     * When this name is encountered when processing UserInfo from a Social Provider, its associated value will be set as the
     * value for all Stormpath Account field names specified in the
     * {@link UserInfoMappingRule#getAccountAttributes() accountAttributes} collection.
     *
     * @param name the UserInfo attribute name that when encountered, should have its value applied as Account field values.
     */
    UserInfoMappingRuleBuilder setName(String name);

    /**
     * Sets the Stormpath account fields that should be updated when encountering {@link UserInfoMappingRule#getName() named}
     * field from the userInfo provided by the social provider.  If discovered, that UserInfo attribute value will be set on
     * all of the Stormpath account fields named in this collection.
     *
     * @param accountAttributes the account fields that should be updated when there's a match with a field name in userInfo
     * provided by the social provider.
     */
    UserInfoMappingRuleBuilder setAccountAttributes(String... accountAttributes);

    /**
     * Sets the Stormpath account fields that should be updated when encountering {@link UserInfoMappingRule#getName() named}
     * field from the userInfo provided by the social provider.  If discovered, that UserInfo attribute value will be set on
     * all of the Stormpath account fields named in this collection.
     *
     * @param accountAttributes the account fields that should be updated when there's a match with a field name in userInfo
     * provided by the social provider.
     */
    UserInfoMappingRuleBuilder setAccountAttributes(Set<String> accountAttributes);

    /**
     * Builds a new {@link UserInfoMappingRule} based on the current state of this builder.
     *
     * @return a new {@link UserInfoMappingRule} to be included in the {@link UserInfoMappingRules} for a Social Provider.
     */
    UserInfoMappingRule build();
}
