package com.stormpath.sdk.impl.challenge.sms;

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.challenge.sms.SmsChallenge;
import com.stormpath.sdk.challenge.sms.SmsChallengeStatus;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.impl.challenge.AbstractChallenge;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 * Created by mehrshadrafiei on 9/26/16.
 */
public class DefaultSmsChallenge extends AbstractChallenge<SmsFactor, SmsChallengeStatus> implements SmsChallenge<SmsFactor, SmsChallengeStatus>{

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
    public Challenge setMessage(String message) {
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
