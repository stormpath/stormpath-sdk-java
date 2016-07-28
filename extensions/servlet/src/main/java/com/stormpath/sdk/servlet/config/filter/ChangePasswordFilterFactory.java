package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.ControllerConfig;
import com.stormpath.sdk.servlet.mvc.ChangePasswordController;

/**
 * @since 1.0.0
 */
public class ChangePasswordFilterFactory extends FormControllerFilterFactory<ChangePasswordController> {

    @Override
    protected ChangePasswordController newController() {
        return new ChangePasswordController();
    }

    @Override
    protected ControllerConfig getResolver(Config config) {
        return config.getChangePasswordConfig();
    }

    @Override
    protected void doConfigure(ChangePasswordController c, Config config) {
        c.setForgotPasswordUri(config.getForgotPasswordConfig().getUri());
        c.setLoginUri(config.getLoginConfig().getUri());
        c.setLoginNextUri(config.getLoginConfig().getNextUri());
        c.setErrorUri(config.getChangePasswordConfig().getErrorUri());
        c.setAutoLogin(config.getChangePasswordConfig().isAutoLogin());
        c.setAuthenticationResultSaver(config.getAuthenticationResultSaver());
    }
}
