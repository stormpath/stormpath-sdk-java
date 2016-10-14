package com.stormpath.sdk.servlet.mvc.provider

import com.stormpath.sdk.provider.LinkedInProvider

class LinkedInAuthorizationEndpointResolverTest extends CommonProviderAuthorizationEndpointResolverTest<LinkedInProvider> {

    @Override
    protected LinkedInAuthorizationEndpointResolver newResolverUT() {
        return new LinkedInAuthorizationEndpointResolver()
    }

    @Override
    protected String getProviderId() {
        "linkedin"
    }

    protected String getBaseUri() {
        "https://www.linkedin.com/uas/oauth2/authorization"
    }


}
