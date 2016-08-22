package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.AccountLinkingPolicy;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.tenant.Tenant;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultAccountLinkingPolicy extends AbstractInstanceResource implements AccountLinkingPolicy {

    // SIMPLE PROPERTIES
    static final StringProperty STATUS = new StringProperty("status");
    static final StringProperty AUTO_PROVISIONING = new StringProperty("autoProvisioning");
    static final StringProperty MATCHING_PROPERTY = new StringProperty("matchingProperty");

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>("tenant", Tenant.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            STATUS, AUTO_PROVISIONING, MATCHING_PROPERTY, TENANT);

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    public DefaultAccountLinkingPolicy(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAccountLinkingPolicy(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    protected static Map<String, Property> createPropertyDescriptorMap(Property... props) {
        Map<String, Property> m = new LinkedHashMap<String, Property>();
        for (Property prop : props) {
            m.put(prop.getName(), prop);
        }
        return m;
    }


    @Override
    public String getStatus() {
        return getString(STATUS);
    }

    @Override
    public AccountLinkingPolicy setStatus(String status) {
        Assert.notNull(status, "status cannot be null.");
        setProperty(STATUS, status);
        return this;
    }

    @Override
    public String getAutoProvisioning() {
        return getString(AUTO_PROVISIONING);
    }

    @Override
    public AccountLinkingPolicy setAutoProvisioning(String autoProvisioningStatus) {
        Assert.notNull(autoProvisioningStatus, "status cannot be null.");
        setProperty(AUTO_PROVISIONING, autoProvisioningStatus);
        return this;
    }

    @Override
    public String getMatchingProperty() {
        return getString(MATCHING_PROPERTY);
    }

    @Override
    public AccountLinkingPolicy setMatchingProperty(String matchingProperty) {
        setProperty(MATCHING_PROPERTY, matchingProperty);
        return this;
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }
}
