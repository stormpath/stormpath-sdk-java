package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.ControllerConfig;
import com.stormpath.sdk.servlet.mvc.RegisterController;

/**
 * @since 1.0.0
 */
public class RegisterFilterFactory extends FormControllerFilterFactory<RegisterController> {

    @Override
    protected RegisterController newController() {
        return new RegisterController();
    }

    @Override
    protected ControllerConfig getResolver(Config config) {
        return config.getRegisterConfig();
    }

    @Override
    protected void doConfigure(RegisterController c, Config config) {
        c.setClient(config.getClient());
        c.setAuthenticationResultSaver(config.getAuthenticationResultSaver());
        c.setLoginUri(config.getLoginConfig().getUri());
        c.setVerifyViewName(config.getVerifyConfig().getView());
        c.setAutoLogin(config.isRegisterAutoLoginEnabled());
        c.setPreRegisterHandler(config.getRegisterPreHandler());
        c.setPostRegisterHandler(config.getRegisterPostHandler());
    }
}
