package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.provider.TwitterProvider;

import java.util.Map;

/**
 * @since 1.3.0
 */
public class DefaultTwitterProvider extends AbstractOAuthProvider<TwitterProvider> implements TwitterProvider {

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PROVIDER_ID, CREATED_AT, MODIFIED_AT, CLIENT_ID, CLIENT_SECRET, SCOPE, USER_INFO_MAPPING_RULES);

    public DefaultTwitterProvider(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultTwitterProvider(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    protected String getConcreteProviderId() {
        return IdentityProviderType.TWITTER.getNameKey();
    }


}

