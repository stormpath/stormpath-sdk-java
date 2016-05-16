package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.mvc.provider.GoogleCallbackController;

import javax.servlet.ServletException;

/**
 * @since 1.0.0
 */
public class GoogleCallbackFilter extends ControllerFilter {

    @Override
    protected void onInit() throws ServletException {
        GoogleCallbackController googleCallbackController = new GoogleCallbackController(
                getConfig().getLoginControllerConfig().getNextUri(),
                getConfig().getAuthenticationResultSaver()
        );

        setController(googleCallbackController);

        super.onInit();
    }
}
