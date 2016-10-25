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
package com.stormpath.sdk.impl.challenge.sms;

import com.stormpath.sdk.challenge.sms.SmsChallenge;
import com.stormpath.sdk.challenge.sms.SmsChallengeStatus;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.impl.challenge.AbstractChallenge;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultSmsChallenge extends AbstractChallenge<SmsFactor, SmsChallengeStatus> implements SmsChallenge{

    static final StringProperty MESSAGE = new StringProperty("message");
    static final StringProperty MESSAGE_ID = new StringProperty("messageId");

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(MESSAGE, MESSAGE_ID);

    public DefaultSmsChallenge(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultSmsChallenge(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        PROPERTY_DESCRIPTORS.putAll(super.getPropertyDescriptors());
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getMessage() {
        return getString(MESSAGE);
    }

    @Override
    public SmsChallenge setMessage(String message) {
        setProperty(MESSAGE, message);
        return this;
    }

    @Override
    public SmsChallengeStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return SmsChallengeStatus.valueOf(value.toUpperCase());
    }

}
