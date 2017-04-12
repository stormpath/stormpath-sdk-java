package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.provider.FacebookProviderData;
import com.stormpath.sdk.provider.OktaProviderData;

import java.util.Date;
import java.util.Map;

/**
 *
 */
public class DefaultOktaProviderData extends AbstractProviderData<OktaProviderData> implements OktaProviderData {

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(CODE);

    public DefaultOktaProviderData(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOktaProviderData(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }


    @Override
    protected String getConcreteProviderId() {
        return IdentityProviderType.OKTA.getNameKey();
    }
}
