package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 * @since 1.3.1
 */
public class DefaultOAuthStormpathFactorChallengeGrantAuthenticationAttempt extends AbstractResource implements OAuthStormpathFactorChallengeGrantAuthenticationAttempt {

    static final StringProperty CHALLENGE = new StringProperty("challenge");
    static final StringProperty CODE = new StringProperty("code");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(CHALLENGE, CODE);

    public DefaultOAuthStormpathFactorChallengeGrantAuthenticationAttempt(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOAuthStormpathFactorChallengeGrantAuthenticationAttempt(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public void setChallenge(String challenge) {
        setProperty(CHALLENGE, challenge);
    }

    @Override
    public void setCode(String code) {
        setProperty(CODE, code);
    }

    public String getChallenge() {
        return getString(CHALLENGE);
    }

    public String getCode() {
        return getString(CODE);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }
}
