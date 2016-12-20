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
package com.stormpath.sdk.impl.provider.social;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.ArrayProperty;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.provider.social.UserInfoMappingRule;
import com.stormpath.sdk.provider.social.UserInfoMappingRules;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @since 1.3.0
 */
public class DefaultUserInfoMappingRules extends AbstractInstanceResource implements UserInfoMappingRules {

    private static final ArrayProperty ITEMS = new ArrayProperty("items", UserInfoMappingRule.class);
    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(CREATED_AT, MODIFIED_AT, ITEMS);

    public DefaultUserInfoMappingRules(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultUserInfoMappingRules(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @SuppressWarnings("unchecked")
    public List<UserInfoMappingRule> getItems() {
        return getListProperty(ITEMS.getName());
    }

    public void setItems(List<UserInfoMappingRule> userInfoMappingRules) {
        setProperty(ITEMS, userInfoMappingRules);
    }

    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Date getModifiedAt() {
        return getDateProperty(MODIFIED_AT);
    }
}
