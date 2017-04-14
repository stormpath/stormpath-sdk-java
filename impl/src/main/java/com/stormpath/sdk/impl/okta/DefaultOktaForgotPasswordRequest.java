package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.okta.OktaForgotPasswordRequest;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 *
 */
public class DefaultOktaForgotPasswordRequest extends AbstractInstanceResource implements OktaForgotPasswordRequest {


    private static final StringProperty USERNAME = new StringProperty("username");
    private static final StringProperty FACTOR_TYPE = new StringProperty("factorType");
    private static final StringProperty RELAY_STATE = new StringProperty("relayState");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(USERNAME, FACTOR_TYPE, RELAY_STATE);

    public DefaultOktaForgotPasswordRequest(InternalDataStore dataStore) {
        super(dataStore);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }
    @Override
    public String getUsername() {
        return getString(USERNAME);
    }

    @Override
    public OktaForgotPasswordRequest setUsername(String username) {
        setProperty(USERNAME, username);
        return this;
    }

    @Override
    public String getFactorType() {
        return getString(FACTOR_TYPE);
    }

    @Override
    public OktaForgotPasswordRequest setFactorType(String factorType) {
        setProperty(FACTOR_TYPE, factorType);
        return this;
    }

    @Override
    public String getRelayState() {
        return getString(RELAY_STATE);
    }

    @Override
    public OktaForgotPasswordRequest setRelayState(String relayState) {
        setProperty(RELAY_STATE, relayState);
        return this;
    }
}
