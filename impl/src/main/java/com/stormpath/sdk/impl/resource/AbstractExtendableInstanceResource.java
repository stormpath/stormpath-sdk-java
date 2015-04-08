/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.resource;

import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.impl.directory.DefaultCustomData;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Extendable;
import com.stormpath.sdk.resource.Saveable;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.0.0
 */
public abstract class AbstractExtendableInstanceResource extends AbstractInstanceResource implements Auditable, Saveable, Extendable {

    public static final ResourceReference<CustomData> CUSTOM_DATA = new ResourceReference<CustomData>("customData", CustomData.class);

    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    protected AbstractExtendableInstanceResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    protected AbstractExtendableInstanceResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore);

        //@since 1.0.0
        if (properties != null && properties.containsKey(CUSTOM_DATA.getName())) {
            Object object = properties.get(CUSTOM_DATA.getName());
            Assert.isInstanceOf(Map.class, object);
            CustomData customData = getDataStore().instantiate(CustomData.class, (Map<String, Object>) properties.get(CUSTOM_DATA.getName()));
            properties.put(CUSTOM_DATA.getName(), customData);
        }
        setProperties(properties);

    }

    @Override
    public CustomData getCustomData() {
        if (isNew() && getResourceProperty(CUSTOM_DATA) == null) {
            setProperty(CUSTOM_DATA, getDataStore().instantiate(CustomData.class));
        }
        return getResourceProperty(CUSTOM_DATA);
    }

    @Override
    public void save(){
        applyCustomDataUpdatesIfNecessary();
        super.save();
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

    /**
     * @since 1.0
     */
    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Date getModifiedAt() {
        return getDateProperty(MODIFIED_AT);
    }
}
