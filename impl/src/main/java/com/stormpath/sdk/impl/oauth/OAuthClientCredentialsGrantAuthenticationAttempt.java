package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.resource.Resource;

/**
 * @since 1.0.0
 */
public interface OAuthClientCredentialsGrantAuthenticationAttempt extends Resource {
    /**
     * Method used to set the Authentication Grant Type that will be used for the token exchange request. Currently only "password" grant type is supported for this operation.
     * @param grantType the Authentication Grant Type that will be used for the token exchange request.
     */
    void setGrantType(String grantType);

    void setApiKeyId(String apiKeyId);

    void setApiKeySecret(String apiKeySecret);
}
