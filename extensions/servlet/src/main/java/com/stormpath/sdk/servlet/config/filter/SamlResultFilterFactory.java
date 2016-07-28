package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.mvc.LogoutController;
import com.stormpath.sdk.servlet.mvc.SamlResultController;

/**
 * @since 1.0.0
 */
public class SamlResultFilterFactory extends ControllerFilterFactory<SamlResultController> {

    @Override
    protected SamlResultController newController() {
        return new SamlResultController();
    }

    @Override
    protected void configure(SamlResultController c, Config config) throws Exception {

        c.setEventPublisher(config.getRequestEventPublisher());

        LogoutController controller = new LogoutController();
        controller.setProduces(config.getProducedMediaTypes());
        controller.setNextUri(config.getLogoutConfig().getNextUri());
        controller.setInvalidateHttpSession(config.isLogoutInvalidateHttpSession());
        controller.init();

        c.setLoginNextUri(getConfig().getLoginConfig().getNextUri());
        c.setLogoutController(controller);
        c.setAuthenticationResultSaver(getConfig().getAuthenticationResultSaver());
        c.setEventPublisher(config.getRequestEventPublisher());
    }
}
