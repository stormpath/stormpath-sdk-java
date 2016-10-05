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
package com.stormpath.sdk.impl.factor.google;

import com.stormpath.sdk.challenge.google.GoogleAuthenticatorChallenge;
import com.stormpath.sdk.factor.FactorType;
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.factor.AbstractFactor;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultGoogleAuthenticatorFactor extends AbstractFactor<GoogleAuthenticatorChallenge> implements GoogleAuthenticatorFactor<GoogleAuthenticatorChallenge> {

    static final StringProperty ACCOUNT_NAME = new StringProperty("accountName");
    static final StringProperty ISSUER = new StringProperty("issuer");
    static final StringProperty SECRET = new StringProperty("secret");
    static final StringProperty KEY_URI = new StringProperty("keyUri");
    static final StringProperty BASE64_QR_IMAGE = new StringProperty("base64QRImage");

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ACCOUNT_NAME, ISSUER, SECRET, KEY_URI, BASE64_QR_IMAGE);

    public DefaultGoogleAuthenticatorFactor(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultGoogleAuthenticatorFactor(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        PROPERTY_DESCRIPTORS.putAll(super.getPropertyDescriptors());
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getAccountName() {
        return getString(ACCOUNT_NAME);
    }

    @Override
    public GoogleAuthenticatorFactor setAccountName(String accountName) {
        setProperty(ACCOUNT_NAME, accountName);
        return this;
    }

    @Override
    public String getIssuer() {
        return getString(ISSUER);
    }

    @Override
    public GoogleAuthenticatorFactor setIssuer(String issuer) {
        setProperty(ISSUER, issuer);
        return this;
    }

    @Override
    public String getSecret() {
        return getString(SECRET);
    }

    @Override
    public String getKeyUri() {
        return getString(KEY_URI);
    }

    @Override
    public String getBase64QrImage() {
        return getString(BASE64_QR_IMAGE);
    }

    protected FactorType getConcreteFactorType() {
        return FactorType.GOOGLE_AUTHENTICATOR;
    }

}
