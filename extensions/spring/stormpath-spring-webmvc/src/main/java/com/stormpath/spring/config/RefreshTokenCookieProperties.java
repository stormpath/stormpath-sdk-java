package com.stormpath.spring.config;

import com.stormpath.sdk.servlet.config.CookieProperties;
import org.springframework.beans.factory.annotation.Value;

/**
 * @since 1.0.0
 */
public class RefreshTokenCookieProperties implements CookieProperties {

    @Value("#{ @environment['stormpath.web.refreshTokenCookie.name'] ?: 'refresh_token' }")
    protected String cookieName;

    @Value("#{ @environment['stormpath.web.refreshTokenCookie.domain'] }")
    protected String cookieDomain;

    @Value("#{ @environment['stormpath.web.refreshTokenCookie.path'] }")
    protected String cookiePath;

    @Value("#{ @environment['stormpath.web.refreshTokenCookie.httpOnly'] ?: true }")
    protected boolean cookieHttpOnly;

    @Value("#{ @environment['stormpath.web.refreshTokenCookie.secure'] ?: true }")
    protected boolean cookieSecure;

    @Override
    public String getCookieName() {
        return cookieName;
    }

    @Override
    public String getCookieComment() {
        return null;
    }

    @Override
    public String getCookieDomain() {
        return cookieDomain;
    }

    @Override
    public String getCookiePath() {
        return cookiePath;
    }

    @Override
    public boolean isCookieHttpOnly() {
        return cookieHttpOnly;
    }

    @Override
    public boolean isCookieSecure() {
        return cookieSecure;
    }

    @Override
    public int getCookieMaxAge() {
        return 0;
    }
}
