package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.provider.TwitterProviderData;

import java.util.Map;

/**
 * @since 1.3.0
 */
public class DefaultTwitterProviderData extends AbstractProviderData<TwitterProviderData> implements TwitterProviderData {

    static final StringProperty ACCESS_TOKEN_SECRET = new StringProperty("accessTokenSecret");

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PROVIDER_ID, CREATED_AT, MODIFIED_AT, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);

    public DefaultTwitterProviderData(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultTwitterProviderData(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        this.setProperty(ACCESS_TOKEN_SECRET, properties.get(ACCESS_TOKEN_SECRET.getName()));
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }


    @Override
    protected String getConcreteProviderId() {
        return IdentityProviderType.TWITTER.getNameKey();
    }


    @Override
    public String getAccessTokenSecret() {
        return getString(ACCESS_TOKEN_SECRET);
    }

    @SuppressWarnings("unchecked")
    public TwitterProviderData setAccessTokenSecret(String accessTokenSecret) {
        setProperty(ACCESS_TOKEN_SECRET, accessTokenSecret);
        return this;
    }
}
