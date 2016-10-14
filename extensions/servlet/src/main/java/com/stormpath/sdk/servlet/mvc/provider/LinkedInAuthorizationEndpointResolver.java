package com.stormpath.sdk.servlet.mvc.provider;

/**
 * @since 1.2.0
 */
public class LinkedInAuthorizationEndpointResolver extends BaseAuthorizationEndpointResolver {

    @Override
    public String getProviderId() {
        return "linkedin";
    }

    @Override
    protected String getBaseUri() {
        return "https://www.linkedin.com/uas/oauth2/authorization";
    }

}
