/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.provider;

/**
 * A provider that may be accessed by clients based on a client ID and secret.
 *
 * @since 1.0
 */
public interface ClientAccessibleProvider extends Provider {

    /**
     * Returns the client application's ID.
     *
     * @return the client application's ID.
     */
    String getClientId();

    /**
     * Returns the client application's secret (credentials) that verifies the {@link #getClientId() clientId}.
     *
     * @return the client application's secret (credentials) that verifies the {@link #getClientId() clientId}.
     */
    String getClientSecret();

}
