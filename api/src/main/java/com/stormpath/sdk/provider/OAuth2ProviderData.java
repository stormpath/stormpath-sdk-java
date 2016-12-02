package com.stormpath.sdk.provider;

/**
 * {@link ProviderData} Resource for any Generic OAuth2 provider.
 *
 * @since 1.3.0
 */
public interface OAuth2ProviderData extends ProviderData {

    /**
     * Getter for the OAuth2 provider's access token.
     *
     * @return the OAuth2 provider's access token.
     */
    String getAccessToken();

    OAuth2ProviderData setProviderId(String providerId);

}
