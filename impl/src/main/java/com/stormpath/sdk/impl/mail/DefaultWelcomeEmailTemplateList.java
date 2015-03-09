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
package com.stormpath.sdk.impl.mail;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractCollectionResource;
import com.stormpath.sdk.impl.resource.ArrayProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.mail.WelcomeEmailTemplate;
import com.stormpath.sdk.mail.WelcomeEmailTemplateList;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public class DefaultWelcomeEmailTemplateList extends AbstractCollectionResource<WelcomeEmailTemplate> implements WelcomeEmailTemplateList {

    private static final ArrayProperty<WelcomeEmailTemplate> ITEMS = new ArrayProperty<WelcomeEmailTemplate>("items", WelcomeEmailTemplate.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(OFFSET, LIMIT, ITEMS);

    public DefaultWelcomeEmailTemplateList(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultWelcomeEmailTemplateList(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    public DefaultWelcomeEmailTemplateList(InternalDataStore dataStore, Map<String, Object> properties, Map<String, Object> queryParams) {
        super(dataStore, properties, queryParams);
    }

    @Override
    protected Class<WelcomeEmailTemplate> getItemType() {
        return WelcomeEmailTemplate.class;
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }
}
