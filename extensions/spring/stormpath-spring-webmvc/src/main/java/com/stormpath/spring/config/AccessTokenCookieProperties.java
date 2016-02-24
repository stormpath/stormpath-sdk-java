package com.stormpath.spring.config;

import com.stormpath.sdk.servlet.config.CookieProperties;
import org.springframework.beans.factory.annotation.Value;

/**
 * @since 1.0.RC10
 */
public class AccessTokenCookieProperties implements CookieProperties {
    @Value("#{ @environment['stormpath.web.accessTokenCookie.name'] ?: 'access_token' }")
    protected String cookieName;

    @Value("#{ @environment['stormpath.web.accessTokenCookie.domain'] }")
    protected String cookieDomain;

    @Value("#{ @environment['stormpath.web.accessTokenCookie.path'] }")
    protected String cookiePath;

    @Value("#{ @environment['stormpath.web.accessTokenCookie.httpOnly'] ?: true }")
    protected boolean cookieHttpOnly;

    @Value("#{ @environment['stormpath.web.accessTokenCookie.secure'] ?: true }")
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
