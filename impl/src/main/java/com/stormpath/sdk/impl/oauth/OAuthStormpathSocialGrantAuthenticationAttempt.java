package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.resource.Resource;

/**
 * @since 1.0.0
 */
public interface OAuthStormpathSocialGrantAuthenticationAttempt extends Resource {
    /**
     * Method used to set the Authentication Grant Type that will be used for the token exchange request.
     * @param grantType the Authentication Grant Type that will be used for the token exchange request.
     */
    void setGrantType(String grantType);

    /**
     * Method used to set the providerId (e.g. google, facebook, linkedin).
     * @param providerId the name of the provider
     */
    void setProviderId(String providerId);

    /**
     * Method used to set the accessToken.
     * @param accessToken the access token from the social provider. The accessToken or code is required.
     */
    void setAccessToken(String accessToken);

    /**
     * Method used to set the code.
     * @param code the code from the social provider. The code or accessToken is required.
     */
    void setCode(String code);
}
