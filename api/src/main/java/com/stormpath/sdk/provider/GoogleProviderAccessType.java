package com.stormpath.sdk.provider;

/**
 * A GoogleProviderAccessType represents the different available options for access_type when initiating
 * OAuth flow with Google.  See
 * <a href="https://developers.google.com/identity/protocols/OAuth2WebServer#offline">Google's documentation</a> for details.
 *
 * @since 1.2
 */
public enum GoogleProviderAccessType {
    OFFLINE, ONLINE
}
