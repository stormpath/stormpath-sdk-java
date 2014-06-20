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
 * <p/>
 * <h3>Usage Example</h3>
 * <pre>
 * import java.util.concurrent.ConcurrentMap;
 * import java.util.concurrent.ConcurrentHashMap;
 *
 *
 * public MyMapNonceStore implements NonceStore {
 *
 *     private ConcurrentMap&lt;String, Object&gt; concurrentMap = new ConcurrentHashMap&lt;String, Object&gt;();
 *
 *     public boolean hasNonce(String nonce) {
 *         returns concurrentMap.containsKey(nonce);
 *     }
 *
 *     public void putNonce(String nonce) {
 *         concurrentMap.putIfAbsent(nonce, nonce);
 *     }
 * }
 * </pre>
 *
 * @see IdSiteAccountResolver#withNonceStore(NonceStore)
 * @since 1.0.RC2
 */
public interface NonceStore {

    /**
     * Checks whether the store contains the provided nonce.
     *
     * @param nonce - The nonce to check.
     * @return - {@code true} if the  provided nonce has been stored in this {@code nonceStore},
     *         false otherwise.
     * @see #putNonce(String)
     */
    boolean hasNonce(String nonce);

    /**
     * Put the given nonce in the store.
     *
     * @param nonce - The nonce to put in this {@code nonceStore}.
     * @see #hasNonce(String)
     */
    void putNonce(String nonce);

}
