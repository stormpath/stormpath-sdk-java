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
import com.stormpath.sdk.provider.social.UserInfoMappingRules;
import com.stormpath.sdk.provider.social.UserInfoMappingRulesBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.3.0
 */
public class DefaultUserInfoMappingRulesBuilder implements UserInfoMappingRulesBuilder {

    private List<UserInfoMappingRule> userInfoMappingRules;

    @Override
    public UserInfoMappingRulesBuilder addUserInfoMappingRule(UserInfoMappingRule userInfoMappingRule) {

        if (this.userInfoMappingRules == null) {
            this.userInfoMappingRules = new ArrayList<>();
        } else {
            Assert.isTrue(!userInfoMappingRules.contains(userInfoMappingRule));
        }
        userInfoMappingRules.add(userInfoMappingRule);
        return this;
    }

    @Override
    public UserInfoMappingRules build() {
        Assert.notEmpty(userInfoMappingRules, "userInfoMappingRules argument cannot be null or empty.");

        UserInfoMappingRules rules = new DefaultUserInfoMappingRules(null);
        rules.setItems(userInfoMappingRules);
        return rules;
    }
}
