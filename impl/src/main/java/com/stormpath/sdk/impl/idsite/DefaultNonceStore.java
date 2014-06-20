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
package com.stormpath.sdk.impl.idsite;

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.impl.ds.DefaultDataStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.idsite.Nonce;
import com.stormpath.sdk.idsite.NonceStore;

import java.util.Map;

/**
 * This is the default implementation of the {@link NonceStore} interface that relies on the
 * {@link DefaultDataStore} cache to store and evict the {@link Nonce} values.
 *
 * @since 1.0.RC
 */
public class DefaultNonceStore implements NonceStore {

    private DefaultDataStore defaultDataStore;

    public DefaultNonceStore(DefaultDataStore defaultDataStore) {
        this.defaultDataStore = defaultDataStore;
    }

    @Override
    public boolean hasNonce(String nonce) {

        Assert.hasText(nonce);

        if (!defaultDataStore.isCachingEnabled()) {
            return false;
        }

        Cache<String, Map<String, ?>> cache = defaultDataStore.getCache(Nonce.class);

        Map<String, ?> values = cache.get(nonce);

        return values != null;
    }

    @Override
    public void putNonce(String nonce) {

        Assert.hasText(nonce);

        if (!defaultDataStore.isCachingEnabled()) {
            return;
        }

        Cache<String, Map<String, ?>> cache = defaultDataStore.getCache(Nonce.class);

        DefaultNonce defaultNonce = new DefaultNonce(nonce);

        cache.put(defaultNonce.getValue(), defaultNonce.getProperties());
    }
}
