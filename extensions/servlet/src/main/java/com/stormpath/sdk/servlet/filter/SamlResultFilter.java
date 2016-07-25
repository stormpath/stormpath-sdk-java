package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.mvc.LogoutController;
import com.stormpath.sdk.servlet.mvc.SamlResultController;

import javax.servlet.ServletException;

/**
 * @since 1.0.0
 */
public class SamlResultFilter extends ControllerFilter {

    @Override
    protected void onInit() throws ServletException {
        Publisher<RequestEvent> eventPublisher = getConfig().getRequestEventPublisher();

        LogoutController logoutController = new LogoutController(getConfig().getLogoutControllerConfig(), getConfig().getProducesMediaTypes());
        logoutController.setLogoutInvalidateHttpSession(getConfig().isLogoutInvalidateHttpSession());

        SamlResultController controller = new SamlResultController();
        controller.setLoginNextUri(getConfig().getLoginControllerConfig().getNextUri());
        controller.setLogoutController(logoutController);
        controller.setAuthenticationResultSaver(getConfig().getAuthenticationResultSaver());
        controller.setEventPublisher(eventPublisher);

        setController(controller);

        super.onInit();
    }
}
