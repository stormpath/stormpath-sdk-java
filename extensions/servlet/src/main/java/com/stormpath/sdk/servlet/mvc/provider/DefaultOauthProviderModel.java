package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.provider.OauthProvider;

/**
 * 1.0.RC8
 */
public class DefaultOauthProviderModel implements OauthProviderModel {

    private final OauthProvider provider;

    public DefaultOauthProviderModel(OauthProvider provider) {
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
