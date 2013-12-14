/*
 * Copyright 2013 Stormpath, Inc.
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

/**
 * Default implementation of the {@link ApiKey} interface.  This implementation is immutable and (therefore) thread-safe.
 *
 * @since 0.1
 */
public class DefaultApiKey implements ApiKey {

    private final String id;

    private final String secret;

    public DefaultApiKey(String id, String secret) {
        if (id == null) {
            throw new IllegalArgumentException("API key id cannot be null.");
        }
        if (secret == null) {
            throw new IllegalArgumentException("API key secret cannot be null.");
        }
        this.id = id;
        this.secret = secret;
    }

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return getId(); //never ever print the secret
    }

    @Override
    public int hashCode() {
        return this.id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof DefaultApiKey) {
            DefaultApiKey other = (DefaultApiKey)o;
            return (id != null ? id.equals(other.id) : other.id == null) &&
                   (secret != null ? secret.equals(other.secret) : other.secret == null);
        }

        return false;
    }
}
