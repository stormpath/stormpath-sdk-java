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
package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationAccountStoreMapping;
import com.stormpath.sdk.impl.accountStoreMapping.AbstractAccountStoreMapping;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.resource.Resource;

import java.util.Map;

/**
 * @since 1.0.RC5.1
 */
public class DefaultApplicationAccountStoreMapping extends AbstractAccountStoreMapping<ApplicationAccountStoreMapping> implements ApplicationAccountStoreMapping {

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Application> APPLICATION = new ResourceReference<Application>("application", Application.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            LIST_INDEX, DEFAULT_ACCOUNT_STORE, DEFAULT_GROUP_STORE, ACCOUNT_STORE, APPLICATION);

    public DefaultApplicationAccountStoreMapping(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultApplicationAccountStoreMapping(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    public ApplicationAccountStoreMapping setApplication(Application application) {
        setResourceProperty(APPLICATION, application);
        return this;
    }

    public Application getApplication(){
        return getResourceProperty(APPLICATION);
    }
}
