/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.api;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.client.ClientCredentials;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.tenant.Tenant;

/**
 * An ApiKey is a secure random username/password pair (called an {@link #getId() id} and
 * {@link #getSecret() secret}) attributed to an {@link Account} that can be used by the account to make secure
 * requests to an API service.
 *
 * <p>An {@link Account Account} may have zero or more ApiKeys, and allowing multiple keys is often useful for key
 * rotation strategies.  For example, a new key can be generated while an existing key is in use.  Applications can
 * then reference the new key (e.g. on startup), and once running, the old key can then be deleted.  This allows for
 * key rotation without an interruption in service, which would happen otherwise if an old key was invalidated the
 * instant a new key was generated.</p>
 *
 * @see #getId()
 * @see #getSecret()
 * @see #getAccount()
 * @since 1.0.RC
 */
public interface ApiKey extends Resource, Saveable, Deletable, ClientCredentials {
    // temporarily extending com.stormpath.sdk.client.ApiKey before deleting it in 1.0 final

    /**
     * Returns the ApiKey ID that uniquely identifies this ApiKey among all others.
     *
     * @return the ApiKey ID that uniquely identifies this ApiKey among all others.
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

    /**
     * Returns the ApiKey status.  ApiKeys that are not {@link ApiKeyStatus#ENABLED ENABLED} cannot be used to
     * authenticate requests.  ApiKeys are enabled by default when they are created.
     *
     * @return the ApiKey status.
     */
    ApiKeyStatus getStatus();

    /**
     * Sets the ApiKey status.  ApiKeys that are not {@link ApiKeyStatus#ENABLED ENABLED} cannot be used to
     * authenticate requests. ApiKeys are enabled by default when they are created.
     *
     * @param status the api key's status.
     */
    void setStatus(ApiKeyStatus status);

    /**
     * Returns the {@link Account} to which this ApiKey belongs.
     *
     * @return the {@link Account} to which this ApiKey belongs.
     */
    Account getAccount();

    /**
     * Returns the Stormpath {@link Tenant} that contains the Account to which the ApiKey belongs.
     *
     * @return the Stormpath {@link Tenant} that contains the Account to which the ApiKey belongs.
     */
    Tenant getTenant();

    /**
     * Saves this ApiKey resource and ensures the server response reflects the specified {@link
     * ApiKeyOptions}.
     *
     * @param options The {@link ApiKeyOptions} to use to customize the ApiKey resource representation returned in the
     *                save response.
     */
    void save(ApiKeyOptions options);
}
