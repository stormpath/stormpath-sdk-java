package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.mvc.provider.FacebookCallbackController;

/**
 * @since 1.0.0
 */
public class FacebookCallbackFilterFactory extends SocialCallbackControllerFilterFactory<FacebookCallbackController> {

    @Override
    protected FacebookCallbackController newController() {
        return new FacebookCallbackController();
    }
}
