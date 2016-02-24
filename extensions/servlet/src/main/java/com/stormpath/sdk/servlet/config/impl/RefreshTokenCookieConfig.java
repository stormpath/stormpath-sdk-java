package com.stormpath.sdk.servlet.config.impl;

import com.stormpath.sdk.servlet.config.CookieProperties;

/**
 * @since 1.0.RC10
 */
public class RefreshTokenCookieConfig extends AbstractCookieConfig {
    private static final String CONFIG_PREFIX = "stormpath.web.refreshTokenCookie";

    @Override
    protected String configPrefix() {
        return CONFIG_PREFIX;
    }

    public RefreshTokenCookieConfig(ConfigReader configReader) {
        super(configReader);
    }

    public RefreshTokenCookieConfig(CookieProperties cookieProperties) {
        super(cookieProperties);
    }
}
