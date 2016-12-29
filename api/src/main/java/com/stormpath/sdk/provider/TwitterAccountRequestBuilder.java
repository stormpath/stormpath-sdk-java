package com.stormpath.sdk.provider;

/**
 * Twitter's specific {@link ProviderAccountRequestBuilder} interface.
 *
 * @since 1.3.0
 */
public interface TwitterAccountRequestBuilder extends ProviderAccountRequestBuilder<TwitterAccountRequestBuilder> {

    /**
     * Setter for the Provider App access token.
     *
     * @param accessToken the Provider App access token.
     * @return the builder instance for method chaining.
     */
    TwitterAccountRequestBuilder setAccessTokenSecret(String accessToken);
}