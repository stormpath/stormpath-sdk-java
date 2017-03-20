package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.directory.OktaPasswordPolicy;
import com.stormpath.sdk.directory.OktaPasswordPolicyList;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractCollectionResource;
import com.stormpath.sdk.impl.resource.ArrayProperty;
import com.stormpath.sdk.impl.resource.Property;

import java.util.Map;

public class DefaultOktaPasswordPolicyList extends AbstractCollectionResource<OktaPasswordPolicy> implements OktaPasswordPolicyList {

    private static final ArrayProperty<OktaPasswordPolicy> ITEMS = new ArrayProperty<>("items", OktaPasswordPolicy.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(OFFSET, LIMIT, ITEMS);

    public DefaultOktaPasswordPolicyList(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOktaPasswordPolicyList(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    public DefaultOktaPasswordPolicyList(InternalDataStore dataStore, Map<String, Object> properties, Map<String, Object> queryParams) {
        super(dataStore, properties, queryParams);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    protected Class<OktaPasswordPolicy> getItemType() {
        return OktaPasswordPolicy.class;
    }
}
