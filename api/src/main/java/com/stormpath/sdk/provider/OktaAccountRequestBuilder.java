package com.stormpath.sdk.provider;

/**
 */
public interface OktaAccountRequestBuilder extends ProviderAccountRequestBuilder<OktaAccountRequestBuilder> {

    /**
     * Setter for the Okta authorization code.
     *
     * @param code the Okta authorization code.
     * @return the Okta authorization code.
     * @since 2.0.0
     */
    OktaAccountRequestBuilder setCode(String code);
}
