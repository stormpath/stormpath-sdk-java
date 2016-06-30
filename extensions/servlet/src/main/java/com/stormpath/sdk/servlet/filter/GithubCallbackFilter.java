package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.mvc.provider.GithubCallbackController;

import javax.servlet.ServletException;

/**
 * @since 1.0.0
 */
public class GithubCallbackFilter extends ControllerFilter {

    @Override
    protected void onInit() throws ServletException {
        GithubCallbackController githubCallbackController = new GithubCallbackController(
                getConfig().getLoginControllerConfig().getNextUri(),
                getConfig().getAuthenticationResultSaver(),
                getConfig().<Publisher<RequestEvent>>getInstance(EVENT_PUBLISHER)
        );

        setController(githubCallbackController);

        super.onInit();
    }
}
