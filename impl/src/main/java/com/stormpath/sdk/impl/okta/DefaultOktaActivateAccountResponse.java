package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.okta.OktaActivateAccountResponse;

import java.util.Map;

/**
 *
 */
public class DefaultOktaActivateAccountResponse extends AbstractInstanceResource implements OktaActivateAccountResponse {

    private static final StringProperty ACTIVATION_URL = new StringProperty("activationUrl");
    private static final StringProperty ACTIVATION_TOKEN = new StringProperty("activationToken");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ACTIVATION_URL, ACTIVATION_TOKEN);

    public DefaultOktaActivateAccountResponse(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOktaActivateAccountResponse(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getActivationUrl() {
        return getString(ACTIVATION_URL);
    }

    @Override
    public String getActivationToken() {
        return getString(ACTIVATION_TOKEN);
    }

    @Override
    public String getHref() {
        return null;
    }
}
