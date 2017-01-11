package com.stormpath.sdk.oauth;

/**
 * This class represents a request to exchange a multifactor authentication code for a valid OAuth 2.0 access token.
 * Using stormpath_factor_challenge grant type
 *
 * @since 1.3.1
 */
public interface OAuthStormpathFactorChallengeGrantRequestAuthentication extends OAuthGrantRequestAuthentication {

    String getChallenge();

    String getCode();
}
