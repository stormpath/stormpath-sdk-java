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
package com.stormpath.sdk.impl.challenge.google;

import com.stormpath.sdk.challenge.google.GoogleAuthenticatorChallenge;
import com.stormpath.sdk.challenge.google.GoogleAuthenticatorChallengeStatus;
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor;
import com.stormpath.sdk.impl.challenge.AbstractChallenge;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.Property;

import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultGoogleAuthenticatorChallenge extends AbstractChallenge<GoogleAuthenticatorFactor, GoogleAuthenticatorChallengeStatus> implements GoogleAuthenticatorChallenge{

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
