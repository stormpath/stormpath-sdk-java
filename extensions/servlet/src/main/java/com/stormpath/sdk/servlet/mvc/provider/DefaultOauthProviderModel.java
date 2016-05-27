package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.provider.OAuthProvider;

/**
 * 1.0.RC8
 */
public class DefaultOAuthProviderModel implements OAuthProviderModel {

    private final OAuthProvider provider;

    public DefaultOAuthProviderModel(OAuthProvider provider) {
        this.provider = provider;
    }

    @Override
    public String getHref() {
        return this.provider.getHref();
    }

    @Override
    public String getProviderId() {
        return this.provider.getProviderId();
    }

    @Override
    public String getClientId() {
        return this.provider.getClientId();
    }
}
