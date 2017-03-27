package com.stormpath.sdk.impl.application.okta;

import com.stormpath.sdk.application.okta.OktaForgotPasswordResult;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 *
 */
public class DefaultOktaForgotPasswordResult extends AbstractInstanceResource implements OktaForgotPasswordResult {

    private static final StringProperty STATUS = new StringProperty("status");
    private static final StringProperty FACTOR_RESULT = new StringProperty("factorResult");
    private static final StringProperty FACTOR_TYPE = new StringProperty("factorType");
    private static final StringProperty RELAY_STATE = new StringProperty("relayState");
    private static final StringProperty RECOVERY_TYPE = new StringProperty("recoveryType");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(STATUS, FACTOR_TYPE, RELAY_STATE, FACTOR_RESULT, RECOVERY_TYPE);

    public DefaultOktaForgotPasswordResult(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOktaForgotPasswordResult(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }


    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getStatus() {
        return getString(STATUS);
    }

    @Override
    public String getFactorResult() {
        return getString(FACTOR_RESULT);
    }

    @Override
    public String getRelayState() {
        return getString(RELAY_STATE);
    }

    @Override
    public String getFactorType() {
        return getString(FACTOR_TYPE);
    }

    @Override
    public String getRecoveryType() {
        return getString(RECOVERY_TYPE);
    }
}
