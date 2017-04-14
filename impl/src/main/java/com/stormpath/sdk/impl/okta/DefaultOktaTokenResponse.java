package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.okta.OktaTokenResponse;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 *
 */
public class DefaultOktaTokenResponse extends AbstractInstanceResource implements OktaTokenResponse {

    private static final StringProperty ACCESS_TOKEN = new StringProperty("access_token");
    private static final StringProperty TOKEN_TYPE = new StringProperty("token_type");
    private static final StringProperty EXPIRES_IN = new StringProperty("expires_in");
    private static final StringProperty SCOPE = new StringProperty("scope");
    private static final StringProperty REFRESH_TOKEN = new StringProperty("refresh_token");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ACCESS_TOKEN, TOKEN_TYPE, EXPIRES_IN, SCOPE, REFRESH_TOKEN);

    public DefaultOktaTokenResponse(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOktaTokenResponse(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getAccessToken() {
        return getString(ACCESS_TOKEN);
    }

    @Override
    public String getTokenType() {
        return getString(TOKEN_TYPE);
    }

    @Override
    public String getExpiresIn() {
        return getString(EXPIRES_IN);
    }

    @Override
    public String getScope() {
        return getString(SCOPE);
    }

    @Override
    public String getRefreshToken() {
        return getString(REFRESH_TOKEN);
    }

    @Override
    public String getIdToken() {
        return null;
    }

    @Override
    public String toJson() {
        return null;
    }

    @Override
    public String getApplicationHref() {
        return null;
    }


}
