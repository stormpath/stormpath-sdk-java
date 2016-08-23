package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultOAuthStormpathSocialGrantAuthenticationAttempt extends AbstractResource implements OAuthStormpathSocialGrantAuthenticationAttempt {

    static final StringProperty GRANT_TYPE = new StringProperty("grant_type");
    static final StringProperty PROVIDER_ID = new StringProperty("providerId");
    static final StringProperty ACCESS_TOKEN = new StringProperty("accessToken");
    static final StringProperty CODE = new StringProperty("code");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(GRANT_TYPE, PROVIDER_ID, ACCESS_TOKEN, CODE);

    public DefaultOAuthStormpathSocialGrantAuthenticationAttempt(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOAuthStormpathSocialGrantAuthenticationAttempt(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public void setGrantType(String grantType) {
        setProperty(GRANT_TYPE, grantType);
    }

    @Override
    public void setProviderId(String providerId) {
        setProperty(PROVIDER_ID, providerId);
    }

    @Override
    public void setAccessToken(String accessToken) {
        setProperty(ACCESS_TOKEN, accessToken);
    }

    @Override
    public void setCode(String code) {
        setProperty(CODE, code);
    }

    public String getGrantType() {
        return getString(GRANT_TYPE);
    }

    public String getProviderId() {
        return getString(PROVIDER_ID);
    }

    public String getAccessToken() {
        return getString(ACCESS_TOKEN);
    }

    public String getCode() {
        return getString(CODE);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }
}
