/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.impl.factor.sms;

import com.stormpath.sdk.challenge.sms.SmsChallenge;
import com.stormpath.sdk.factor.FactorType;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.factor.AbstractFactor;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.phone.Phone;

import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultSmsFactor extends AbstractFactor<SmsChallenge> implements SmsFactor<SmsChallenge> {
    static final ResourceReference<Phone> PHONE = new ResourceReference<>("phone", Phone.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PHONE);

    public DefaultSmsFactor(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultSmsFactor(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        PROPERTY_DESCRIPTORS.putAll(super.getPropertyDescriptors());
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public Phone getPhone() {
        return getResourceProperty(PHONE);
    }

    @Override
    public SmsFactor setPhone(Phone phone) {
        if(phone.getHref() != null) {
            setResourceProperty(PHONE, phone);
        }
        else{
            setMaterializableResourceProperty(PHONE, phone);
        }
        return this;
    }

    @Override
    public SmsFactor challenge() {
        String href = getHref();
        href += "/challenges";
        Assert.notNull(href, "SmsFactor hast to be materialized and have an href.");
        return getDataStore().create(href, this);
    }

    @Override
    protected FactorType getConcreteFactorType() {
        return FactorType.SMS;
    }
}
