package com.stormpath.sdk.impl.application.okta;

import com.stormpath.sdk.application.okta.OktaTokenRequest;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 *
 */
public class DefaultOktaTokenRequest extends AbstractInstanceResource implements OktaTokenRequest {

    private static final StringProperty USERNAME = new StringProperty("username");
    private static final StringProperty PASSWORD = new StringProperty("password");
    private static final StringProperty REDIRECT_URI = new StringProperty("redirect_uri");
    private static final StringProperty SCOPE = new StringProperty("scope");
    private static final StringProperty GRANT_TYPE = new StringProperty("grant_type");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(USERNAME, PASSWORD, REDIRECT_URI, SCOPE, GRANT_TYPE);

    public DefaultOktaTokenRequest(InternalDataStore dataStore) {
        super(dataStore);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return null;
    }

    @Override
    public String getGrantType() {
        return getString(GRANT_TYPE);
    }

    @Override
    public OktaTokenRequest setGrantType(String grantType) {
        setProperty(GRANT_TYPE, grantType);
        return this;
    }

    @Override
    public String getUsername() {
        return getString(USERNAME);
    }

    @Override
    public OktaTokenRequest setUsername(String username) {
        setProperty(USERNAME, username);
        return this;
    }

    @Override
    public String getPassword() {
        return getString(PASSWORD);
    }

    @Override
    public OktaTokenRequest setPassword(String password) {
        setProperty(PASSWORD, password);
        return this;
    }

    @Override
    public String getRedirectUri() {
        return getString(REDIRECT_URI);
    }

    @Override
    public OktaTokenRequest setRedirectUri(String redirectUri) {
        setProperty(REDIRECT_URI, redirectUri);
        return this;
    }

    @Override
    public String getScope() {
        return getString(SCOPE);
    }

    @Override
    public OktaTokenRequest setScope(String scope) {
        setProperty(SCOPE, scope);
        return this;
    }
}
