/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.client;

public interface ClientCredentials {

    /**
     * Returns the Client ID that uniquely this client among all others.
     *
     * @return the  Client ID  that uniquely identifies this client among all others.
     */
    String getId();

    /**
     * Returns the ApiKey plaintext secret - a very secret, very private value that should never be disclosed to anyone
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
     * @return the ApiKey plaintext secret
     */
    String getSecret();
}
