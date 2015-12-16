package com.stormpath.sdk.provider;

/**
 * @since 1.0.RC8
 */
public interface RedirectableOauthProvider extends OauthProvider {

    /**
     * Returns the URI to where the oauth provider should redirect the browser and provide the access token.
     *
     * @return the URI to where the oauth provider should redirect the browser and provide the access token.
     * @since 1.0.RC5
     */
    String getRedirectUri();
}
