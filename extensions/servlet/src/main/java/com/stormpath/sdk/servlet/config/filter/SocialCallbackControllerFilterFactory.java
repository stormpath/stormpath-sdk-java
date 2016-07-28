package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.mvc.AbstractSocialCallbackController;

/**
 * @since 1.0.0
 */
public abstract class SocialCallbackControllerFilterFactory<T extends AbstractSocialCallbackController>
    extends ControllerFilterFactory<T> {

    @Override
    protected final void configure(T c, Config config) throws Exception {
        c.setNextUri(config.getLoginConfig().getUri());
        c.setAuthenticationResultSaver(config.getAuthenticationResultSaver());
        c.setEventPublisher(config.getRequestEventPublisher());
        c.setApplicationResolver(config.getApplicationResolver());
    }
}
