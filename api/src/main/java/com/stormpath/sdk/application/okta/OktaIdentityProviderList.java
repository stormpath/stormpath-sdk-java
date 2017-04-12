package com.stormpath.sdk.application.okta;

import com.stormpath.sdk.provider.OktaProvider;
import com.stormpath.sdk.resource.Resource;

import java.util.Set;

/**
 *
 */
public interface OktaIdentityProviderList extends Resource {


    Set<OktaProvider> getIdentityProviders();

}
