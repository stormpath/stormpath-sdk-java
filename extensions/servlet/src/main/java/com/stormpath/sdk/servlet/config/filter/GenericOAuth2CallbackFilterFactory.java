package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.mvc.provider.GenericOAuth2ProviderCallbackController;

/**
 * @since 1.3.0
 */
public class GenericOAuth2CallbackFilterFactory extends SocialCallbackControllerFilterFactory<GenericOAuth2ProviderCallbackController> {

    @Override
    protected GenericOAuth2ProviderCallbackController newController() {
        return new GenericOAuth2ProviderCallbackController();
    }
}
