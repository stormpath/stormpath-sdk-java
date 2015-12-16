package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.directory.Directory;

/**
 * 1.0.RC8
 */
public class DefaultAccountStoreModel implements AccountStoreModel {

    private final Directory directory;
    private final ProviderModel providerModel;

    public DefaultAccountStoreModel(Directory directory, ProviderModel provider) {
        this.directory = directory;
        this.providerModel = provider;
    }

    @Override
    public String getHref() {
        return this.directory.getHref();
    }

    @Override
    public String getName() {
        return this.directory.getName();
    }

    @Override
    public ProviderModel getProvider() {
        return this.providerModel;
    }
}
