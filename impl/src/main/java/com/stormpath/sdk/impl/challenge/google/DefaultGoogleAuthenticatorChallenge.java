package com.stormpath.sdk.impl.challenge.google;

import com.stormpath.sdk.challenge.google.GoogleAuthenticatorChallenge;
import com.stormpath.sdk.challenge.google.GoogleAuthenticatorChallengeStatus;
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor;
import com.stormpath.sdk.impl.challenge.AbstractChallenge;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.Property;

import java.util.Map;

/**
 * Created by mehrshadrafiei on 9/26/16.
 */
public class DefaultGoogleAuthenticatorChallenge extends AbstractChallenge<GoogleAuthenticatorFactor, GoogleAuthenticatorChallengeStatus> implements GoogleAuthenticatorChallenge<GoogleAuthenticatorFactor, GoogleAuthenticatorChallengeStatus>{

    static final Map<String, Property> PROPERTY_DESCRIPTORS = AbstractChallenge.PROPERTY_DESCRIPTORS;

    public DefaultGoogleAuthenticatorChallenge(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultGoogleAuthenticatorChallenge(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return super.getPropertyDescriptors();
    }


    @Override
    public GoogleAuthenticatorChallengeStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return GoogleAuthenticatorChallengeStatus.valueOf(value.toUpperCase());
    }
}
