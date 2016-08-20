package com.stormpath.sdk.authc;

/**
 * Credentials to be used when authenticating requests to the Stormpath API server.
 * @since 1.0.3
 */
public interface StormpathCredentials {
    /**
     * Returns the ID that uniquely identifies these Client Credentials among all others.
     *
     * @return the ID that uniquely identifies these Client Credentials among all others.
     */
    String getId();
    /**
     * Returns the client credentials plaintext secret - a very secret, very private value that should never be disclosed to anyone
     * other than the actual account holder.  The secret value is mostly used for computing HMAC digests, but can also
     * be used as a password for password-based key derivation and encryption.
     *
     * <h3>Security Notice</h3>
     *
     * <p>Stormpath SDKs automatically encrypt this value at rest and in SDK cache to prevent plaintext access.  The
     * plaintext value is only available by calling this method, which returns the plaintext (unencrypted) value.
     * Please use this method with caution and only when necessary to ensure your API users' secrets remain
     * secure.
     *
     * @return the client credentials plaintext secret
     */
    String getSecret();
}
