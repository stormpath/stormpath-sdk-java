/*
 *
 *  * Copyright 2014 Stormpath, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.stormpath.sdk.impl.api;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.api.ApiKeyStatus;
import com.stormpath.sdk.tenant.Tenant;

/**
 * This implementation represents the api key that is used to authenticate a Tenant in Stormpath.
 * @since 1.0.RC
 */
public class ClientApiKey implements ApiKey {

    private final String id;

    private final String secret;

    private static final String METHOD_ERROR_MESSAGE = "This method is not accessible from a client api key. Only getId() and getSecret() are available from this instance.";

    public ClientApiKey(String id, String secret) {
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
    public ApiKeyStatus getStatus() {
        throw new IllegalAccessError(METHOD_ERROR_MESSAGE);
    }

    @Override
    public void setStatus(ApiKeyStatus status) {
        throw new IllegalAccessError(METHOD_ERROR_MESSAGE);
    }

    @Override
    public Account getAccount() {
        throw new IllegalAccessError(METHOD_ERROR_MESSAGE);
    }

    @Override
    public Tenant getTenant() {
        throw new IllegalAccessError(METHOD_ERROR_MESSAGE);
    }

    @Override
    public void save(ApiKeyOptions options) {
        throw new IllegalAccessError(METHOD_ERROR_MESSAGE);
    }

    @Override
    public void delete() {
        throw new IllegalAccessError(METHOD_ERROR_MESSAGE);
    }

    @Override
    public String getHref() {
        throw new IllegalAccessError(METHOD_ERROR_MESSAGE);
    }

    @Override
    public void save() {
        throw new IllegalAccessError(METHOD_ERROR_MESSAGE);
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
        if (o instanceof ClientApiKey) {
            ClientApiKey other = (ClientApiKey)o;
            return (id != null ? id.equals(other.id) : other.id == null) &&
                    (secret != null ? secret.equals(other.secret) : other.secret == null);
        }

        return false;
    }
}
