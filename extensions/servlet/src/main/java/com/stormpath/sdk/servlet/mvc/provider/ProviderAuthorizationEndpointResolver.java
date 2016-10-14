package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.provider.Provider;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.2.0
 */
public interface ProviderAuthorizationEndpointResolver {

    /**
     * The provider id that this endpoint resolver should be used for
     *
     * @return provider id for the concrete implementation
     */
    String getProviderId();

    /**
     * The provider authorization endpoint (including query parameters)
     *
     * @param request  for the flow
     * @param provider provider of the directory that should be used to authenticate
     * @return the provider authorization endpoint
     */
    String getEndpoint(HttpServletRequest request, Provider provider);
}