package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.okta.OIDCWellKnownResource;

import java.util.Map;

/**
 */
public class DefaultOIDCWellKnownResource extends AbstractInstanceResource implements OIDCWellKnownResource {


    private static final StringProperty AUTHORIZATION_ENDPOINT = new StringProperty("authorization_endpoint");
    private static final StringProperty INTROSPECTION_ENDPOINT = new StringProperty("introspection_endpoint");
    private static final StringProperty JWKS_URI = new StringProperty("jwks_uri");
    private static final StringProperty REVOCATION_ENDPOINT = new StringProperty("revocation_endpoint");
    private static final StringProperty TOKEN_ENDPOINT = new StringProperty("token_endpoint");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(AUTHORIZATION_ENDPOINT, INTROSPECTION_ENDPOINT, JWKS_URI, REVOCATION_ENDPOINT, TOKEN_ENDPOINT);

    public DefaultOIDCWellKnownResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOIDCWellKnownResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getAuthorizationEndpoint() {
        return getString(AUTHORIZATION_ENDPOINT);
    }

    @Override
    public OIDCWellKnownResource setAuthorizationEndpoint(String authorizationEndpoint) {
        setProperty(AUTHORIZATION_ENDPOINT, authorizationEndpoint);
        return this;
    }

    @Override
    public String getIntrospectionEndpoint() {
        return getString(INTROSPECTION_ENDPOINT);
    }

    @Override
    public OIDCWellKnownResource setIntrospectionEndpoint(String introspectionEndpoint) {
        setProperty(INTROSPECTION_ENDPOINT, introspectionEndpoint);
        return this;
    }

    @Override
    public String getJwksUri() {
        return getString(JWKS_URI);
    }

    @Override
    public OIDCWellKnownResource setJwksUri(String jwksUri) {
        setProperty(JWKS_URI, jwksUri);
        return this;
    }

    @Override
    public String getRevocationEndpoint() {
        return getString(REVOCATION_ENDPOINT);
    }

    @Override
    public OIDCWellKnownResource setRevocationEndpoint(String revocationEndpoint) {
        setProperty(REVOCATION_ENDPOINT, revocationEndpoint);
        return this;
    }

    @Override
    public String getTokenEndpoint() {
        return getString(TOKEN_ENDPOINT);
    }

    @Override
    public OIDCWellKnownResource setTokenEndpoint(String tokenEndpoint) {
        setProperty(TOKEN_ENDPOINT, tokenEndpoint);
        return this;
    }
}
