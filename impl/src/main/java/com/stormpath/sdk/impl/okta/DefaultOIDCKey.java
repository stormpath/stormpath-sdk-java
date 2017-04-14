package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.okta.OIDCKey;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 *
 */
public class DefaultOIDCKey extends AbstractInstanceResource implements OIDCKey {

    private static final StringProperty ALGORITHM = new StringProperty("alg");
    private static final StringProperty TYPE = new StringProperty("kty");
    private static final StringProperty USE = new StringProperty("use");
    private static final StringProperty ID = new StringProperty("kid");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ALGORITHM);

    public DefaultOIDCKey(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getAlgorithm() {
        return getString(ALGORITHM);
    }

    @Override
    public String getId() {
        return getString(ID);
    }

    @Override
    public String getType() {
        return getString(TYPE);
    }

    @Override
    public String getUse() {
        return getString(USE);
    }

    @Override
    public String get(String id) {
        return getStringProperty(id);
    }
}
