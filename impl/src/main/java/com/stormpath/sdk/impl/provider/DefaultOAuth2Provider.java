package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.provider.AccessTokenType;
import com.stormpath.sdk.provider.OAuth2Provider;

import java.util.Map;

/**
 * @since 1.3.0
 */
public class DefaultOAuth2Provider extends AbstractOAuthProvider<OAuth2Provider> implements OAuth2Provider {

    private static final StringProperty AUTHORIZAION_ENDPOINT = new StringProperty("authorizationEndpoint");
    private static final StringProperty TOKEN_ENDPOINT = new StringProperty("tokenEndpoint");
    private static final StringProperty RESOURCE_ENDPOINT = new StringProperty("resourceEndpoint");
    private static final EnumProperty<AccessTokenType> ACCESS_TOKEN_TYPE = new EnumProperty<>("accessTokenType", AccessTokenType.class);

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PROVIDER_ID, CREATED_AT,
            MODIFIED_AT, CLIENT_ID, CLIENT_SECRET, SCOPE, USER_INFO_MAPPING_RULES, AUTHORIZAION_ENDPOINT,
            TOKEN_ENDPOINT, RESOURCE_ENDPOINT, ACCESS_TOKEN_TYPE);

    public DefaultOAuth2Provider(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOAuth2Provider(InternalDataStore dataStore, String providerId) {
        super(dataStore);
        setProviderId(providerId);
    }

    public DefaultOAuth2Provider(InternalDataStore dataStore, Map<String, Object> properties) {
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

    @Override
    public String getAuthorizationEndpoint() {
        return getString(AUTHORIZAION_ENDPOINT);
    }

    @Override
    public String getTokenEndpoint() {
        return getString(TOKEN_ENDPOINT);
    }

    @Override
    public String getResourceEndpoint() {
        return getString(RESOURCE_ENDPOINT);
    }

    @Override
    public AccessTokenType getAccessType() {
        String value = getStringProperty(ACCESS_TOKEN_TYPE.getName());
        if (value == null) {
            return null;
        }
        return AccessTokenType.fromNameKey(value);
    }

    public OAuth2Provider setProviderId(String providerId) {
        setProperty(PROVIDER_ID, providerId);
        return this;
    }


    public OAuth2Provider setAuthorizationEndpoint(String authorizationEndpoint) {
        setProperty(AUTHORIZAION_ENDPOINT, authorizationEndpoint);
        return this;
    }

    public OAuth2Provider setTokenEndpoint(String tokenEndpoint) {
        setProperty(TOKEN_ENDPOINT, tokenEndpoint);
        return this;
    }

    public OAuth2Provider setResourceEndpoint(String resourceEndpoint) {
        setProperty(RESOURCE_ENDPOINT, resourceEndpoint);
        return this;
    }

    public OAuth2Provider setAccessTokenType(AccessTokenType accessTokenType) {
        setProperty(ACCESS_TOKEN_TYPE, accessTokenType);
        return this;
    }



}
