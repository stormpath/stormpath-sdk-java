package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 * @since 1.0.0
 */
public class DefaultOAuthClientCredentialsGrantAuthenticationAttempt extends AbstractResource implements OAuthClientCredentialsGrantAuthenticationAttempt {

    static final StringProperty GRANT_TYPE = new StringProperty("grant_type");
    static final StringProperty API_KEY_ID = new StringProperty("apiKeyId");
    static final StringProperty API_KEY_SECRET = new StringProperty("apiKeySecret");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(GRANT_TYPE, API_KEY_ID, API_KEY_SECRET);

    public DefaultOAuthClientCredentialsGrantAuthenticationAttempt(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOAuthClientCredentialsGrantAuthenticationAttempt(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public void setGrantType(String grantType) {
        setProperty(GRANT_TYPE, grantType);
    }

    @Override
    public void setApiKeyId(String apiKeyId) {
        setProperty(API_KEY_ID, apiKeyId);
    }

    @Override
    public void setApiKeySecret(String apiKeySecret) {
        setProperty(API_KEY_SECRET, apiKeySecret);
    }

    public String getGrantType() {
        return getString(GRANT_TYPE);
    }

    public String getApiKeyId() {
        return getString(API_KEY_ID);
    }

    public String getApiKeySecret() {
        return getString(API_KEY_SECRET);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }
}
