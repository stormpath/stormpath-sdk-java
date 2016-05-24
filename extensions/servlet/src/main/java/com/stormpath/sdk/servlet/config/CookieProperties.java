package com.stormpath.sdk.servlet.config;

/**
 * @since 1.0.0
 */
public interface CookieProperties {

    String getCookieName();

    String getCookieComment();

    String getCookieDomain();

    int getCookieMaxAge();

    String getCookiePath();

    boolean isCookieHttpOnly();

    boolean isCookieSecure();
}
