package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.mvc.AuthorizeCallbackController;

/**
 * @since 1.2.0
 */
public class AuthorizeCallbackFilterFactory extends ControllerFilterFactory<AuthorizeCallbackController> {

    @Override
    protected AuthorizeCallbackController newController() {
        return new AuthorizeCallbackController();
    }

    @Override
    protected void configure(AuthorizeCallbackController c, Config config) throws Exception {
        c.setAuthenticationResultSaver(config.getAuthenticationResultSaver());
        c.setProviderAccountRequestResolver(config.getProviderAccountRequestResolver());
    }
}
