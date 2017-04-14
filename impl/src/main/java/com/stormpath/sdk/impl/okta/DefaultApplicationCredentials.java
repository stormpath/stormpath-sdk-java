package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.okta.ApplicationCredentials;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 *
 */
public class DefaultApplicationCredentials extends AbstractInstanceResource implements ApplicationCredentials {

    private final static StringProperty CLIENT_ID = new StringProperty("client_id");
    private final static StringProperty CLIENT_SECRET = new StringProperty("client_secret");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(CLIENT_ID, CLIENT_SECRET);

    public DefaultApplicationCredentials(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultApplicationCredentials(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    public String getClientId() {
        return getString(CLIENT_ID);
    }

    public ApplicationCredentials setClientId(String clientId) {
        setProperty(CLIENT_ID, clientId);
        return this;
    }

    public String getClientSecret() {
        return getString(CLIENT_SECRET);
    }

    public ApplicationCredentials setClientSecret(String clientSecret) {
        setProperty(CLIENT_SECRET, clientSecret);
        return this;
    }
}
