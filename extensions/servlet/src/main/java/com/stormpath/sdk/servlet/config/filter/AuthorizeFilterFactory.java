package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.mvc.AuthorizeController;

/**
 * @since 1.2.0
 */
public class AuthorizeFilterFactory extends ControllerFilterFactory<AuthorizeController> {
    @Override
    protected AuthorizeController newController() {
        return new AuthorizeController();
    }

    @Override
    protected void configure(AuthorizeController c, Config config) throws Exception {
        c.setNextUri(config.getLoginConfig().getUri());
        c.setApplicationResolver(config.getApplicationResolver());
        c.setClient(config.getClient());
        c.setProviderAuthorizationEndpointResolver(config.getProviderAuthorizationEndpointResolver());
    }
}
