package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.mvc.provider.FacebookCallbackController;

import javax.servlet.ServletException;

/**
 * @since 1.0.0
 */
public class FacebookCallbackFilter extends ControllerFilter {

    @Override
    protected void onInit() throws ServletException {
        FacebookCallbackController facebookCallbackController = new FacebookCallbackController(
                getConfig().getLoginControllerConfig().getNextUri(),
                getConfig().getAuthenticationResultSaver()
        );

        setController(facebookCallbackController);

        super.onInit();
    }
}
