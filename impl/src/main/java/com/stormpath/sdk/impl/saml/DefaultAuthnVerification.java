package com.stormpath.sdk.impl.saml;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.saml.AuthnVerification;
import com.stormpath.sdk.saml.RegisteredSamlServiceProvider;

import java.util.Map;

public class DefaultAuthnVerification extends AbstractResource implements AuthnVerification {

    static final ResourceReference<RegisteredSamlServiceProvider> SERVICE_PROVIDER = new ResourceReference<>("serviceProvider", RegisteredSamlServiceProvider.class);
    static final StringProperty RELAY_STATE = new StringProperty("relayState");
    static final StringProperty REQUEST_ID = new StringProperty("requestId");

    static final Map<String, Property> PROPERTY_DESCRIPTORS;

    static {
        PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(new Property[] { SERVICE_PROVIDER, RELAY_STATE, REQUEST_ID });
    }

    public DefaultAuthnVerification(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAuthnVerification(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getRelayState() {
        return getString(RELAY_STATE);
    }

    @Override
    public RegisteredSamlServiceProvider getServiceProvider() {
        return getResourceProperty(SERVICE_PROVIDER);
    }

    @Override
    public String getRequestId() {
        return getString(REQUEST_ID);
    }
}
