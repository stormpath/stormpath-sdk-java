package com.stormpath.sdk.servlet.mvc.provider;

/**
 * @since 1.2.0
 */
public class FacebookAuthorizationEndpointResolver extends BaseAuthorizationEndpointResolver {
    @Override
    public String getProviderId() {
        return "facebook";
    }

    @Override
    protected String getBaseUri() {
        return "https://www.facebook.com/v2.8/dialog/oauth";
    }
}
