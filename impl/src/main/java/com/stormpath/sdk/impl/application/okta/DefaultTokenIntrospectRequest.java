package com.stormpath.sdk.impl.application.okta;

import com.stormpath.sdk.application.okta.TokenIntrospectRequest;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 *
 */
public class DefaultTokenIntrospectRequest extends AbstractInstanceResource implements TokenIntrospectRequest {

    private static final StringProperty TOKEN = new StringProperty("token");
    private static final StringProperty TOKEN_TYPE_HINT = new StringProperty("token_type_hint");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(TOKEN, TOKEN_TYPE_HINT);


    public DefaultTokenIntrospectRequest(InternalDataStore dataStore) {
        super(dataStore);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getToken() {
        return getString(TOKEN);
    }

    @Override
    public TokenIntrospectRequest setToken(String token) {
        setProperty(TOKEN, token);
        return this;
    }

    @Override
    public String getTokenTypeHint() {
        return getString(TOKEN_TYPE_HINT);
    }

    @Override
    public TokenIntrospectRequest setTokenTypeHint(String tokenTypeHint) {
        setProperty(TOKEN_TYPE_HINT, tokenTypeHint);
        return this;
    }
}
