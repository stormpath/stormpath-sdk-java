package com.stormpath.sdk.servlet.mvc.provider;

/**
 * @since 1.2.0
 */
public class GoogleAuthorizationEndpointResolver extends BaseAuthorizationEndpointResolver {
    @Override
    public String getProviderId() {
        return "google";
    }

    @Override
    protected String getBaseUri() {
        return "https://accounts.google.com/o/oauth2/auth";
    }
}
