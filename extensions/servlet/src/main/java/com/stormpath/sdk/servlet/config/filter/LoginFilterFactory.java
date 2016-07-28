package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.ControllerConfig;
import com.stormpath.sdk.servlet.mvc.LoginController;

/**
 * @since 1.0.0
 */
public class LoginFilterFactory extends FormControllerFilterFactory<LoginController> {

    @Override
    protected LoginController newController() {
        return new LoginController();
    }

    @Override
    protected ControllerConfig getResolver(Config config) {
        return config.getLoginConfig();
    }

    @Override
    protected void doConfigure(LoginController c, Config config) {
        c.setForgotPasswordEnabled(config.getForgotPasswordConfig().isEnabled());
        c.setForgotLoginUri(config.getForgotPasswordConfig().getUri());
        c.setVerifyEnabled(config.getVerifyConfig().isEnabled());
        c.setVerifyUri(config.getVerifyConfig().getUri());
        c.setRegisterEnabledResolver(config.getRegisterEnabledResolver());
        c.setRegisterUri(config.getRegisterConfig().getUri());
        c.setLogoutUri(config.getLogoutConfig().getUri());
        c.setAuthenticationResultSaver(config.getAuthenticationResultSaver());
        c.setPreLoginHandler(config.getLoginPreHandler());
        c.setPostLoginHandler(config.getLoginPostHandler());
        c.setIdSiteEnabled(config.isIdSiteEnabled());
        c.setCallbackEnabled(config.isCallbackEnabled());
    }
}
