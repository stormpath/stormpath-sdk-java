package com.stormpath.sdk.servlet.oauth.impl;

/**
 * Access Token implementation of the {@link com.stormpath.sdk.servlet.oauth.OAuthTokenResolver} interface.
 *
 * @since 1.0.0
 */
public class AccessTokenResolver extends AbstractOAuthTokenResolver {

    public static String REQUEST_ATTR_NAME = AccessTokenResolver.class.getName();

    /**
     * A thread-safe instance to use as desired.
     */
    public static final AccessTokenResolver INSTANCE = new AccessTokenResolver();

    @Override
    protected String getRequestAttributeName() {
        return REQUEST_ATTR_NAME;
    }
}
