package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.servlet.mvc.provider.ProviderAuthorizationEndpointResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * This {@link ProviderAuthorizationEndpointResolver} takes a list of other
 * {@link ProviderAuthorizationEndpointResolver}s.  Then getEndpoint delegates to the getEndpoint of the
 * {@link ProviderAuthorizationEndpointResolver} that matches the {@link Provider} passed in.
 *
 * @since 1.2.0
 */
public class DelegatingAuthorizationEndpointResolver implements ProviderAuthorizationEndpointResolver {
    private final Map<String, ProviderAuthorizationEndpointResolver> resolverMap;

    public DelegatingAuthorizationEndpointResolver(ProviderAuthorizationEndpointResolver... resolvers) {
        resolverMap = new HashMap<>(resolvers.length);
        for (ProviderAuthorizationEndpointResolver resolver : resolvers) {
            resolverMap.put(resolver.getProviderId(), resolver);
        }
    }

    @Override
    public String getProviderId() {
        return "N/A";
    }

    @Override
    public String getEndpoint(HttpServletRequest request, Provider provider) {
        ProviderAuthorizationEndpointResolver resolver = resolverMap.get(provider.getProviderId());
        Assert.notNull(resolver, "No ProviderAuthroizationEndpointResolver was found for " + provider.getProviderId());
        return resolver.getEndpoint(request, provider);
    }
}
