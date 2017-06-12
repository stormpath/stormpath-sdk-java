package com.stormpath.sdk.impl.resource;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.resource.VoidResource;

import java.util.Map;

public class DefaultVoidResource extends AbstractInstanceResource implements VoidResource {

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap();
    private String href;

    public DefaultVoidResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultVoidResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    public DefaultVoidResource(InternalDataStore dataStore, Map<String, Object> properties, String href) {
        this(dataStore, properties);
        this.href = href;
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getHref() {
        if (href != null) {
            return href;
        }
        return super.getHref();
    }
}
