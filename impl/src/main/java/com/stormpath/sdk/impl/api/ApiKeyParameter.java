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

/**
 * @since 1.0.RC
 */
public enum ApiKeyParameter {

    ID("id"),
    ENCRYPT_SECRET("encryptSecret"),
    ENCRYPTION_KEY_SIZE("encryptionKeySize"),
    ENCRYPTION_KEY_ITERATIONS("encryptionKeyIterations"),
    ENCRYPTION_KEY_SALT("encryptionKeySalt");

    private final String name;

    private ApiKeyParameter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
