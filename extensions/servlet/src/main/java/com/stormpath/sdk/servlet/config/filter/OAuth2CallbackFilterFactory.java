package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.mvc.provider.OAuth2ProviderCallbackController;

/**
 * @since 1.3.0
 */
public class OAuth2CallbackFilterFactory extends SocialCallbackControllerFilterFactory<OAuth2ProviderCallbackController> {

    @Override
    protected OAuth2ProviderCallbackController newController() {
        return new OAuth2ProviderCallbackController();
    }
}
