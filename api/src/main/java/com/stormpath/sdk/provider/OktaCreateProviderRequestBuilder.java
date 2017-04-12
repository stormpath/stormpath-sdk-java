package com.stormpath.sdk.provider;

/**
 * Okta-specific {@link com.stormpath.sdk.provider.ProviderRequestFactory} interface.
 */
public interface OktaCreateProviderRequestBuilder extends CreateProviderRequestBuilder<OktaCreateProviderRequestBuilder> {

    /**
     * Setter for the redirection Uri for your Okta Application.
     *
     * @param redirectUri the redirection Uri for your Okta.
     * @return the builder instance for method chaining.
     * @since 2.0.0
     */
    OktaCreateProviderRequestBuilder setRedirectUri(String redirectUri);

}
