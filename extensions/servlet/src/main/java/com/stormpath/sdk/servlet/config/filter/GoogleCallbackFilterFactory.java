package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.mvc.provider.GoogleCallbackController;

/**
 * @since 1.0.0
 */
public class GoogleCallbackFilterFactory extends SocialCallbackControllerFilterFactory<GoogleCallbackController> {

    @Override
    protected GoogleCallbackController newController() {
        return new GoogleCallbackController();
    }
}
