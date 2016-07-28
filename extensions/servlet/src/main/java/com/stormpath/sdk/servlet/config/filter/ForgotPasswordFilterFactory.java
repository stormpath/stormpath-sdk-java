package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.ControllerConfig;
import com.stormpath.sdk.servlet.mvc.ForgotPasswordController;

/**
 * @since 1.0.0
 */
public class ForgotPasswordFilterFactory extends FormControllerFilterFactory<ForgotPasswordController> {

    @Override
    protected ForgotPasswordController newController() {
        return new ForgotPasswordController();
    }

    @Override
    protected ControllerConfig getResolver(Config config) {
        return config.getLoginConfig();
    }

    @Override
    protected void doConfigure(ForgotPasswordController c, Config config) {
        c.setLoginUri(config.getLoginConfig().getUri());
        c.setAccountStoreResolver(config.getAccountStoreResolver());
    }
}