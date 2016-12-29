package com.stormpath.sdk.provider;

/**
 * Twitter-specific {@link com.stormpath.sdk.provider.ProviderData} Resource.
 *
 * @since 1.3.0
 */
public interface TwitterProviderData extends ProviderData {

    /**
     * Getter for the Twitter access token.
     *
     * @return the Twitter access token.
     */
    String getAccessToken();

    /**
     * Getter for the Twitter access token secret.
     *
     * @return the Twitter access token secret.
     */
    String getAccessTokenSecret();

}
