package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.mvc.provider.TwitterCallbackController;

/**
 * @since 1.3.0
 */
public class TwitterCallbackFilterFactory extends SocialCallbackControllerFilterFactory<TwitterCallbackController> {

    @Override
    protected TwitterCallbackController newController() {
        return new TwitterCallbackController();
    }
}
