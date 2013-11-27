/*
 * Copyright 2013 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.lang.Assert;

import java.util.Map;

/**
 * AbstractDirectoryEntity is an abstract representation for resources that depend on hang off of a
 * {@link com.stormpath.sdk.directory.Directory} like {@link com.stormpath.sdk.account.Account} or
 * {@link com.stormpath.sdk.group.Group}.
 *
 * @since 0.9
 */
public abstract class AbstractDirectoryEntity extends AbstractInstanceResource {

    public static final ResourceReference<CustomData> CUSTOM_DATA = new ResourceReference<CustomData>("customData", CustomData.class);

    protected AbstractDirectoryEntity(InternalDataStore dataStore) {
        super(dataStore);
    }

    protected AbstractDirectoryEntity(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore);

        if (properties != null && properties.containsKey(CUSTOM_DATA.getName())) {
            Object object = properties.get(CUSTOM_DATA.getName());
            Assert.isInstanceOf(Map.class, object);
            CustomData customData = getDataStore().instantiate(CustomData.class, (Map<String, Object>) properties.get(CUSTOM_DATA.getName()));
            properties.put(CUSTOM_DATA.getName(), customData);
        }
        setProperties(properties);
    }

    protected CustomData getCustomData(){
        if (isNew() && getResourceProperty(CUSTOM_DATA) == null) {
            setProperty(CUSTOM_DATA, getDataStore().instantiate(CustomData.class));
        }
        return getResourceProperty(CUSTOM_DATA);
    }

    protected void applyCustomDataUpdatesIfNecessary(){
        CustomData customData = getCustomData();
        Assert.isInstanceOf(DefaultCustomData.class, customData);

        DefaultCustomData customDataImpl = (DefaultCustomData) customData;

        if(customDataImpl.isDirty()){
            if(customDataImpl.hasRemovedProperties()){
                customDataImpl.deleteRemovedProperties();
            }
            if(customDataImpl.hasNewProperties()){
                setProperty(CUSTOM_DATA, customData);
            }
        }
    }
}
