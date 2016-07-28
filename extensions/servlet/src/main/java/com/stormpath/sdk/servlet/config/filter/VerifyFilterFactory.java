package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.ControllerConfig;
import com.stormpath.sdk.servlet.mvc.VerifyController;

/**
 * @since 1.0.0
 */
public class VerifyFilterFactory extends FormControllerFilterFactory<VerifyController> {

    @Override
    protected VerifyController newController() {
        return new VerifyController();
    }

    @Override
    protected ControllerConfig getResolver(Config config) {
        return config.getVerifyConfig();
    }

    @Override
    protected void doConfigure(VerifyController c, Config config) {
        c.setLoginUri(config.getLoginConfig().getUri());
        c.setLoginNextUri(config.getLoginConfig().getNextUri());
        c.setClient(config.getClient());
        c.setAutoLogin(config.isRegisterAutoLoginEnabled());
        c.setAuthenticationResultSaver(config.getAuthenticationResultSaver());
        c.setAccountStoreResolver(config.getAccountStoreResolver());
    }
}
