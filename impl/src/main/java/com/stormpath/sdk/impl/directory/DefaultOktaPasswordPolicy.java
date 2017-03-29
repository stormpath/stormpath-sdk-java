package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.directory.OktaPasswordPolicy;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.IntegerProperty;
import com.stormpath.sdk.impl.resource.MapProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Date;
import java.util.Map;

public class DefaultOktaPasswordPolicy extends AbstractInstanceResource implements OktaPasswordPolicy {

    // SIMPLE PROPERTIES
    static final StringProperty TYPE = new StringProperty("type");
    static final StringProperty ID = new StringProperty("id");
    static final StringProperty STATUS = new StringProperty("status");
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final IntegerProperty PRIORITY = new IntegerProperty("priority");
    static final BooleanProperty SYSTEM = new BooleanProperty("system");
    static final DateProperty CREATED = new DateProperty("created");
    static final DateProperty LAST_UPDATED = new DateProperty("lastUpdated");

    // MAP Properties
    static final MapProperty CONDITIONS = new MapProperty("conditions");
    static final MapProperty SETTINGS = new MapProperty("settings");
    static final MapProperty DELEGATION = new MapProperty("delegation");
    static final MapProperty RULES = new MapProperty("rules");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
        TYPE, ID, STATUS, NAME, DESCRIPTION, PRIORITY, SYSTEM, CREATED, LAST_UPDATED, CONDITIONS, SETTINGS, DELEGATION, RULES
    );

    public DefaultOktaPasswordPolicy(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOktaPasswordPolicy(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }


    @Override
    public String getType() {
        return getString(TYPE);
    }

    @Override
    public String getId() {
        return getString(ID);
    }

    @Override
    public String getStatus() {
        return getString(STATUS);
    }

    @Override
    public String getName() {
        return getString(NAME);
    }

    @Override
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @Override
    public int getPriority() {
        return getInt(PRIORITY);
    }

    @Override
    public boolean getSystem() {
        return getBoolean(SYSTEM);
    }

    @Override
    public Map<String, Object> getConditions() {
        return getMap(CONDITIONS);
    }

    @Override
    public Date getCreated() {
        return getDateProperty(CREATED);
    }

    @Override
    public Date getLastUpdated() {
        return getDateProperty(LAST_UPDATED);
    }

    @Override
    public Map<String, Object> getSettings() {
        return getMap(SETTINGS);
    }

    @Override
    public Map<String, Object> getDelegation() {
        return getMap(DELEGATION);
    }

    @Override
    public Map<String, Object> getRules() {
        return getMap(RULES);
    }
}
