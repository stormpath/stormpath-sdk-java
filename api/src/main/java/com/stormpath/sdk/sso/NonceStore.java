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
package com.stormpath.sdk.sso;

/**
 * NonceStore is a configurable
 *
 * @since 1.0.RC
 */
public interface NonceStore {

    /**
     * Checks whether the store contains the provided nonce.
     *
     * @param nonce - The nonce to check.
     * @return - {@code true} if the  provided nonce has been recently put in this {@code nonceStore},
     *         false otherwise.
     */
    boolean hasNonce(String nonce);

    /**
     * Put the given nonce in the store.
     *
     * @param nonce - The nonce to put in this {@code nonceStore}.
     */
    void putNonce(String nonce);

}
