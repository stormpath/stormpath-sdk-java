package com.stormpath.sdk.impl.oauth.authc;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationResult;
import com.stormpath.sdk.oauth.permission.TokenResponse;

import java.util.Map;

/**
 * @since 1.0.RC
 */
public class DefaultBasicOauthAuthenticationResult extends DefaultOauthAuthenticationResult implements BasicOauthAuthenticationResult {

    public DefaultBasicOauthAuthenticationResult(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultBasicOauthAuthenticationResult(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public TokenResponse getTokenResponse() {
        throw new UnsupportedOperationException("getTokenResponse() method hasn't been implemented.");
    }
}
