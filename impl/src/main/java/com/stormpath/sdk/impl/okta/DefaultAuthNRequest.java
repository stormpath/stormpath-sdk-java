package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.okta.AuthNRequest;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.MapProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 *
 */
public class DefaultAuthNRequest extends AbstractInstanceResource implements AuthNRequest {

    private final static StringProperty USERNAME = new StringProperty("username");
    private final static StringProperty PASSWORD = new StringProperty("password");

    private final static MapProperty OPTIONS = new MapProperty("options");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(USERNAME, PASSWORD, OPTIONS);

    public DefaultAuthNRequest(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAuthNRequest(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return null;
    }

    @Override
    public String getUsername() {
        return getString(USERNAME);
    }

    @Override
    public AuthNRequest setUsername(String username) {
        setProperty(USERNAME, username);
        return this;
    }

    @Override
    public String getPassword() {
        return getString(PASSWORD);
    }

    @Override
    public AuthNRequest setPassword(String password) {
        setProperty(PASSWORD, password);
        return this;
    }

    @Override
    public Map<String, Object> getOptions() {
        return getMap(OPTIONS);
    }

    @Override
    public AuthNRequest setOptions(Map<String, Object> options) {
        setProperty(OPTIONS, options);
        return this;
    }
}
