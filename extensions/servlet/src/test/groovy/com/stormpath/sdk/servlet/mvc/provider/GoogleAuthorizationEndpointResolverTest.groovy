package com.stormpath.sdk.servlet.mvc.provider

class GoogleAuthorizationEndpointResolverTest extends CommonProviderAuthorizationEndpointResolverTest {
    @Override
    protected BaseAuthorizationEndpointResolver newResolverUT() {
        return new GoogleAuthorizationEndpointResolver()
    }

    @Override
    protected String getProviderId() {
        return "google"
    }

    @Override
    protected String getBaseUri() {
        return "https://accounts.google.com/o/oauth2/auth"
    }
}
