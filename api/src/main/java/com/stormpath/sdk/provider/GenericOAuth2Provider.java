package com.stormpath.sdk.provider;

/**
 * {@link Provider} Resource for any Generic OAuth2 provider.
 *
 * @since 1.3.0
 */
public interface GenericOAuth2Provider extends OAuthProvider {

    GenericOAuth2Provider setProviderId(String providerId);

    String getAuthorizationEndpoint();

    String getTokenEndpoint();

    String getResourceEndpoint();

    AccessTokenType getAccessType();
}