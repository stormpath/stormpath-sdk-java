package com.stormpath.sdk.servlet.mvc.provider

import com.stormpath.sdk.provider.GithubProvider

class GithubAuthorizationEndpointResolverTest extends CommonProviderAuthorizationEndpointResolverTest<GithubProvider> {
    @Override
    protected BaseAuthorizationEndpointResolver newResolverUT() {
        return new GithubAuthorizationEndpointResolver()
    }

    @Override
    protected String getProviderId() {
        return "github"
    }

    @Override
    protected String getBaseUri() {
        return "https://github.com/login/oauth/authorize"
    }
}
