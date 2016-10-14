package com.stormpath.sdk.servlet.mvc.provider

class FacebookeAuthorizationEndpointResolverTest extends CommonProviderAuthorizationEndpointResolverTest {
    @Override
    protected BaseAuthorizationEndpointResolver newResolverUT() {
        return new FacebookAuthorizationEndpointResolver()
    }

    @Override
    protected String getProviderId() {
        return "facebook"
    }

    @Override
    protected String getBaseUri() {
        return "https://www.facebook.com/v2.8/dialog/oauth"
    }
}
