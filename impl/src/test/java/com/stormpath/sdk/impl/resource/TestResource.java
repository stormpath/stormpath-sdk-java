package com.stormpath.sdk.impl.resource;

import com.stormpath.sdk.impl.ds.InternalDataStore;

import java.util.Map;

public class TestResource extends AbstractResource {

    protected TestResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    protected TestResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public String getName() {
        return super.getStringProperty("name");
    }

    public void setName(String name) {
        super.setProperty("name", name);
    }

    public String getDescription() {
        return super.getStringProperty("description");
    }

    public void setDescription(String description) {
        super.setProperty("description", description);
    }
}