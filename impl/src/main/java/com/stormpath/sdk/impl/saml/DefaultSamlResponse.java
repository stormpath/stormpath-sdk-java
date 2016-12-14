package com.stormpath.sdk.impl.saml;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.saml.SamlResponse;

import java.util.Map;

public class DefaultSamlResponse extends AbstractResource implements SamlResponse {

    static final StringProperty VALUE = new StringProperty("value");

    static final Map<String, Property> PROPERTY_DESCRIPTORS;

    static {
        PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(new Property[] { VALUE });
    }

    public DefaultSamlResponse(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultSamlResponse(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getValue() {
        return getString(VALUE);
    }
}
