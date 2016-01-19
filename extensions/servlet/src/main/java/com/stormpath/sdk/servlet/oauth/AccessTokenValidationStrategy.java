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
package com.stormpath.sdk.servlet.oauth;

/**
 * @since 1.0.RC8
 */
public enum AccessTokenValidationStrategy {

    /**
     * Local validation. See {@link com.stormpath.sdk.oauth.JwtAuthenticator#withLocalValidation() JwtAuthenticator#withLocalValidation()}.
     */
    LOCAL,

    /**
     * Validation against Stormpath's backend. See {@link com.stormpath.sdk.oauth.JwtAuthenticator}.
     */
    STORMPATH;

    /**
     * Returns the {@code AccessTokenValidationStrategy} instance associated with the specified String name.
     *
     * @param name The name of the {@code AccessTokenValidationStrategy} instance to retrieve.
     * @return the {@code AccessTokenValidationStrategy} instance associated with the specified String name.
     * @throws IllegalArgumentException if {@code name} is null or does not match (case insensitive) a valid AccessTokenValidationStrategy
     *                                  value.
     */
    public static AccessTokenValidationStrategy fromName(String name) {
        for (AccessTokenValidationStrategy method : values()) {
            if (method.name().equalsIgnoreCase(name)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unrecognized method name: " + name);
    }

}
