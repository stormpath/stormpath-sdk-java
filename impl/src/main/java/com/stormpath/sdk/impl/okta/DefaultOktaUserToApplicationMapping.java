package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.MapProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.okta.OktaUserToApplicationMapping;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 */
public class DefaultOktaUserToApplicationMapping extends AbstractInstanceResource implements OktaUserToApplicationMapping {

    private static final StringProperty ID = new StringProperty("id");
    private static final StringProperty SCOPE = new StringProperty("scope");
    private static final StringProperty USERNAME = new StringProperty("userName");
    private static final MapProperty CREDENTIALS = new MapProperty("credentials");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ID, SCOPE, USERNAME);

    public DefaultOktaUserToApplicationMapping(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOktaUserToApplicationMapping(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }


    @Override
    public String getId() {
        return getString(ID);
    }

    @Override
    public OktaUserToApplicationMapping setId(String id) {
        setProperty(ID, id);
        return this;
    }

    @Override
    public String getScope() {
        return getString(SCOPE);
    }

    @Override
    public OktaUserToApplicationMapping setScope(String scope) {
        setProperty(SCOPE, scope);
        return this;
    }

    @Override
    public String getUsername() {
        Map credMap = getMap(CREDENTIALS);
        if (!Collections.isEmpty(credMap)) {
            return (String) credMap.get(USERNAME.getName());
        }
        return null;
    }

    @Override
    public OktaUserToApplicationMapping setUsername(String username) {
        Map<String, String> credMap = new LinkedHashMap<>();
        credMap.put(USERNAME.getName(), username);
        setProperty(CREDENTIALS, credMap);
        return this;
    }
}
