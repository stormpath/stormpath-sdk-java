package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.mvc.provider.LinkedinCallbackController;

/**
 * @since 1.0.0
 */
public class LinkedInCallbackFilterFactory extends SocialCallbackControllerFilterFactory<LinkedinCallbackController> {

    @Override
    protected LinkedinCallbackController newController() {
        return new LinkedinCallbackController();
    }
}
