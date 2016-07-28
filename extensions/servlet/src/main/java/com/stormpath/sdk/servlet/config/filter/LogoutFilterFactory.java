package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.mvc.LogoutController;

/**
 * @since 1.0.0
 */
public class LogoutFilterFactory extends ControllerFilterFactory<LogoutController> {

    @Override
    protected LogoutController newController() {
        return new LogoutController();
    }

    @Override
    protected void configure(LogoutController c, Config config) throws Exception {
        c.setProduces(config.getProducedMediaTypes());
        c.setNextUri(config.getLogoutConfig().getNextUri());
        //c.setInvalidateHttpSession(); support this in plain servlet mode?
    }
}
