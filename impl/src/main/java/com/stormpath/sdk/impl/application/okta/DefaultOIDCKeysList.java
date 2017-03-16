package com.stormpath.sdk.impl.application.okta;

import com.stormpath.sdk.application.okta.OIDCKey;
import com.stormpath.sdk.application.okta.OIDCKeysList;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.SetProperty;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class DefaultOIDCKeysList extends AbstractInstanceResource implements OIDCKeysList {

    private static final SetProperty KEYS = new SetProperty("keys", OIDCKey.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(KEYS);

    public DefaultOIDCKeysList(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public Set<OIDCKey> getKeys() {

        Set<OIDCKey> keys = new HashSet<>();
        for (Object item : getSetProperty(KEYS.getName())) {
            keys.add(getDataStore().instantiate(OIDCKey.class, (Map<String, Object>) item));
        }

        return keys;
    }

    @Override
    public OIDCKey getKeyById(String keyId) {

        for (OIDCKey key : getKeys()) {
            if (keyId.equals(key.getId())) {
                return key;
            }
        }
        return null;
    }
}
