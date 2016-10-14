package com.stormpath.sdk.servlet.mvc.provider;

/**
 * @since 1.2.0
 */
public class GithubAuthorizationEndpointResolver extends BaseAuthorizationEndpointResolver {
    @Override
    public String getProviderId() {
        return "github";
    }

    @Override
    protected String getBaseUri() {
        return "https://github.com/login/oauth/authorize";
    }
}
