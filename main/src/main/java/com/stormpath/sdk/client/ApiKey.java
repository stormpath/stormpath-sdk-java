/*
 * Copyright 2012 Stormpath, Inc.
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
 * An ApiKey represents an account's API-specific ID and secret.  All Stormpath REST invocations must be authenticated
 * with an ApiKey.
 *
 * @since 0.1
 */
public interface ApiKey {

    /**
     * Returns the public unique identifier.
     *
     * @return the public unique identifier.
     */
    String getId();

    /**
     * Returns the raw SECRET used for API authentication. <b>NEVER EVER</b> print this value anywhere - logs,
     * files, etc.  It is TOP SECRET.
     *
     * @return the raw SECRET used for API authentication
     */
    String getSecret();
}
