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
package com.stormpath.sdk.idsite;

/**
 * Store {@code nonce} values and provides methods to check if a nonce has already been used, and to
 * add it to the {@code nonceStore} to ensure that the same nonce cannot be used again.
 *
 * <h5>Implementation Warning</h5>
 *
 * <p>NonceStore implementations <b>MUST</b> support TTL (Time-to-Live) policies and automatic eviction of entries
 * that are older than a configured TTL.  If an implementation does not support TTL eviction, the store will fill up
 * indefinitely over time, likely causing storage errors.</p>
 *
 * <p>Because of the TTL requirement, most NonceStore implementations delegate to a Caching API that supports TTL
 * eviction.</p>
 *
 * <p><b>NOTE:</b>If you enable caching in the Stormpath SDK, the SDK will automatically enable a default cache-based
 * NonceStore implementation for you.  Just ensure that your caching configuration uses a default cache TTL slightly
 * greater than 1 minute (the valid lifespan of a ID Site reply message).</p>
 *
 * @see IdSiteCallbackHandler#setNonceStore(NonceStore)
 * @since 1.0.RC2
 */
public interface NonceStore {

    /**
     * {@code true} if the specified nonce is present in this {@code nonceStore}, {@code false} otherwise.
     *
     * @param nonce the nonce to check.
     * @return {@code true} if the specified nonce is present in this {@code nonceStore}, {@code false} otherwise.
     * @see #putNonce(String)
     */
    boolean hasNonce(String nonce);

    /**
     * Adds the specified nonce to the store.
     *
     * @param nonce the nonce to put in this {@code nonceStore}.
     * @see #hasNonce(String)
     */
    void putNonce(String nonce);

}
