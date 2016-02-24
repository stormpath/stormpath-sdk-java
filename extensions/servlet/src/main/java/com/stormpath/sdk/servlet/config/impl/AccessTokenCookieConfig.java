package com.stormpath.sdk.servlet.config.impl;

import com.stormpath.sdk.servlet.config.CookieProperties;

/**
 * @since 1.0.RC10
 */
public class AccessTokenCookieConfig extends AbstractCookieConfig {
    private static final String CONFIG_PREFIX = "stormpath.web.accessTokenCookie";

    @Override
    protected String configPrefix() {
        return CONFIG_PREFIX;
    }

    public AccessTokenCookieConfig(ConfigReader configReader) {
        super(configReader);
    }

    public AccessTokenCookieConfig(CookieProperties cookieProperties) {
        super(cookieProperties);
    }
}
