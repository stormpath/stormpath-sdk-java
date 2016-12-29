package com.stormpath.sdk.provider;

/**
 * {@link CreateProviderRequestBuilder} interface for any Generic OAuth2 provider.
 *
 * @since 1.3.0
 */
public interface GenericOAuth2CreateProviderRequestBuilder extends CreateProviderRequestBuilder<GenericOAuth2CreateProviderRequestBuilder> {

    /**
     * Setter for the authorizationEndpoint for the OAuth2 provider.
     *
     * @param authorizationEndpoint the authorizationEndpoint for the OAuth2 provider.
     * @return the builder instance for method chaining.
     */
    GenericOAuth2CreateProviderRequestBuilder setAuthorizationEndpoint(String authorizationEndpoint);

    /**
     * Setter for the tokenEndpoint for the OAuth2 provider.
     *
     * @param tokenEndpoint the tokenEndpoint for the OAuth2 provider.
     * @return the builder instance for method chaining.
     */
    GenericOAuth2CreateProviderRequestBuilder setTokenEndpoint(String tokenEndpoint);

    /**
     * Setter for the resourceEndpoint for the OAuth2 provider.
     *
     * @param resourceEndpoint the resourceEndpoint for the OAuth2 provider.
     * @return the builder instance for method chaining.
     */
    GenericOAuth2CreateProviderRequestBuilder setResourceEndpoint(String resourceEndpoint);

    /**
     * Setter for the accessTokenType for the OAuth2 provider.
     *
     * @param accessTokenType the accessTokenType for the OAuth2 provider.
     * @return the builder instance for method chaining.
     */
    GenericOAuth2CreateProviderRequestBuilder setAccessTokenType(AccessTokenType accessTokenType);

    /**
     * Setter for the provider id of the OAuth2 provider(e.g. "amazon").
     *
     * @param providerId the provider id of the OAuth2 provider.
     * @return the builder instance for method chaining.
     */
    GenericOAuth2CreateProviderRequestBuilder setClientId(String providerId);

    GenericOAuth2CreateProviderRequestBuilder setProviderId(String providerId);
}
