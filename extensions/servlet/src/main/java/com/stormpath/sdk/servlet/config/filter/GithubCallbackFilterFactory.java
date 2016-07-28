package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.mvc.provider.GithubCallbackController;

/**
 * @since 1.0.0
 */
public class GithubCallbackFilterFactory extends SocialCallbackControllerFilterFactory<GithubCallbackController> {

    @Override
    protected GithubCallbackController newController() {
        return new GithubCallbackController();
    }
}
