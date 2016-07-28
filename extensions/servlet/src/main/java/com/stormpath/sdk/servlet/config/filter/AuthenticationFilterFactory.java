package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.AuthenticationFilter;

/**
 * @since 1.0.0
 */
public class AuthenticationFilterFactory extends AccessControlFilterFactory<AuthenticationFilter> {

    @Override
    protected AuthenticationFilter newInstance() {
        return new AuthenticationFilter();
    }

    @Override
    protected void configure(AuthenticationFilter f, Config config) {
        f.setLoginUrl(config.getLoginConfig().getUri());
        f.setAccessTokenUrl(config.getAccessTokenUrl());
    }
}
