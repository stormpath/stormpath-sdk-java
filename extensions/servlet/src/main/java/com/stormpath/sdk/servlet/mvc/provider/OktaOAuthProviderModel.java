package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.provider.OktaProvider;

/**
 */
public class OktaOAuthProviderModel extends DefaultOAuthProviderModel {
    private final OktaProvider provider;

    public OktaOAuthProviderModel(OktaProvider provider) {
        super(provider);
        this.provider = provider;
    }

    public String getIdp() {
        return provider.getIdp();
    }
}