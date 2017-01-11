package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.resource.Resource;

/**
 * @since 1.3.1
 */
public interface OAuthStormpathFactorChallengeGrantAuthenticationAttempt extends Resource {
    /**
     * Method used to set the href of the challenge to be verified.
     * @param challenge the href of the challenge to be verified.
     */
    void setChallenge(String challenge);

    /**
     * Method used to set the code for the multifactor challenge.
     * @param code the code for the multifactor challenge.
     */
    void setCode(String code);
}
