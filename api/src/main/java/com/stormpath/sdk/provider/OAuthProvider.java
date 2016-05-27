package com.stormpath.sdk.provider;

/**
 * @since 1.0.RC8
 */
public interface OAuthProvider extends Provider {

    /**
     * Returns the client ID used to authenticate requests to the 3rd party oauth provider.
     *
     * @return the client ID used to authenticate requests to the 3rd party oauth provider.
     */
    String getClientId();

    /**
     * Returns the client secret used to authenticate requests to the 3rd party oauth provider.
     *
     * @return the client secret used to authenticate requests to the 3rd party oauth provider.
     */
    String getClientSecret();
}
