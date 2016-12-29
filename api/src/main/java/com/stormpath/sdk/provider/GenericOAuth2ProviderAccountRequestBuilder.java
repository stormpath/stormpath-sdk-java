package com.stormpath.sdk.provider;

/**
 * {@link ProviderAccountRequestBuilder} interface for any Generic OAuth2 provider.
 *
 * @since 1.3.0
 */
public interface GenericOAuth2ProviderAccountRequestBuilder extends ProviderAccountRequestBuilder<GenericOAuth2ProviderAccountRequestBuilder> {

    /**
     * Setter for the provider id of the OAuth2 Provider (as provided when creating the provider directory).
     *
     * @param providerId the provider id of the OAuth2 Provider
     * @return the builder instance for method chaining.
     */
    GenericOAuth2ProviderAccountRequestBuilder setProviderId(String providerId);
}
