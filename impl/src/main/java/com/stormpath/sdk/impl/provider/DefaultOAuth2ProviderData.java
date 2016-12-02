package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.provider.OAuth2ProviderData;

import java.util.Map;

/**
 * @since 1.3.0
 */
public class DefaultOAuth2ProviderData extends AbstractProviderData<OAuth2ProviderData> implements OAuth2ProviderData {

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PROVIDER_ID, CREATED_AT, MODIFIED_AT, ACCESS_TOKEN, CODE);

    public DefaultOAuth2ProviderData(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOAuth2ProviderData(InternalDataStore dataStore, String providerId) {
        super(dataStore);
        setProviderId(providerId);
    }

    public DefaultOAuth2ProviderData(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        if (properties.containsKey(PROVIDER_ID.getName())) {
            setProviderId((String) properties.get(PROVIDER_ID.getName()));
        }
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }


    @Override
    protected String getConcreteProviderId() {
        return getProviderId();
    }


    public OAuth2ProviderData setProviderId(String providerId) {
        setProperty(PROVIDER_ID, providerId);
        return this;
    }
}
