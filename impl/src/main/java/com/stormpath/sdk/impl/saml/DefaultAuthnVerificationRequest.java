package com.stormpath.sdk.impl.saml;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.saml.AuthnVerification;
import com.stormpath.sdk.saml.AuthnVerificationRequest;

import java.util.Map;

public class DefaultAuthnVerificationRequest extends AbstractResource implements AuthnVerificationRequest {
    static final StringProperty SAML_REQUEST = new StringProperty("samlRequest");
    static final StringProperty RELAY_STATE = new StringProperty("relayState");
    static final StringProperty SIG_ALG = new StringProperty("sigAlg");
    static final StringProperty SIGNATURE = new StringProperty("signature");
    static final StringProperty RAW_QUERY_STRING = new StringProperty("rawQueryString");

    static final Map<String, Property> PROPERTY_DESCRIPTORS;

    static {
        PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(new Property[] { SAML_REQUEST, SIG_ALG, SIGNATURE, RAW_QUERY_STRING });
    }

    public DefaultAuthnVerificationRequest(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAuthnVerificationRequest(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getSamlRequest() {
        return getString(SAML_REQUEST);
    }

    @Override
    public AuthnVerificationRequest setSamlRequest(String samlRequest) {
        setProperty(SAML_REQUEST, samlRequest);
        return this;
    }

    @Override
    public String getRelayState() {
        return getString(RELAY_STATE);
    }

    @Override
    public AuthnVerificationRequest setRelayState(String relayState) {
        setProperty(RELAY_STATE, relayState);
        return this;
    }

    @Override
    public String getSigAlg() {
        return getString(SIG_ALG);
    }

    @Override
    public AuthnVerificationRequest setSigAlg(String sigAlg) {
        setProperty(SIG_ALG, sigAlg);
        return this;
    }

    @Override
    public String getSignature() {
        return getString(SIGNATURE);
    }

    @Override
    public AuthnVerificationRequest setSignature(String signature) {
        setProperty(SIGNATURE, signature);
        return this;
    }

    @Override
    public String getRawQueryString() {
        return getString(RAW_QUERY_STRING);
    }

    @Override
    public AuthnVerificationRequest setRawQueryString(String rawQueryString) {
        setProperty(RAW_QUERY_STRING, rawQueryString);
        return this;
    }


}
