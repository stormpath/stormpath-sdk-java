package com.stormpath.sdk.servlet.oauth.impl;

/**
 * Refresh Token implementation of the {@link com.stormpath.sdk.servlet.oauth.OAuthTokenResolver} interface.
 *
 * @since 1.0.RC10
 */
public class RefreshTokenResolver extends AbstractOAuthTokenResolver {

    public static String REQUEST_ATTR_NAME = RefreshTokenResolver.class.getName();

    /**
     * A thread-safe instance to use as desired.
     */
    public static final RefreshTokenResolver INSTANCE = new RefreshTokenResolver();

    @Override
    protected String getRequestAttributeName() {
        return REQUEST_ATTR_NAME;
    }
}
