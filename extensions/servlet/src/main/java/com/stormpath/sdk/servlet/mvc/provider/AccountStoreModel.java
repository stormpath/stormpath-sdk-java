package com.stormpath.sdk.servlet.mvc.provider;

/**
 * 1.0.RC8
 */
public interface AccountStoreModel {

    String getHref();

    String getName();

    ProviderModel getProvider();
}
