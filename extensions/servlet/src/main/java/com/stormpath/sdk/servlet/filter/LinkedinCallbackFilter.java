package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.mvc.provider.LinkedinCallbackController;

import javax.servlet.ServletException;

/**
 * @since 1.0.0
 */
public class LinkedinCallbackFilter extends ControllerFilter {
    @Override
    protected void onInit() throws ServletException {

        LinkedinCallbackController linkedinCallbackController = new LinkedinCallbackController(
                getConfig().getLoginControllerConfig().getNextUri(),
                getConfig().getAuthenticationResultSaver()
        );

        setController(linkedinCallbackController);

        super.onInit();
    }
}
