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
package com.stormpath.sdk.provider;

/**
 * A Builder to construct {@link ProviderAccountRequest}s.
 *
 * @param <T> the specific builder class (e.g {@link FacebookAccountRequestBuilder} or {@link GoogleAccountRequestBuilder}.
 *
 * @since 1.0.beta
 */
public interface ProviderAccountRequestBuilder<T extends ProviderAccountRequestBuilder<T>> {

    /**
     * Setter for the Provider App authorization code.
     *
     * @param accessToken the Provider App authorization code.
     * @return the builder instance for method chaining.
     */
    T setAccessToken(String accessToken);

    /**
     * Creates a new {@code ProviderAccountRequest} instance based on the current builder state.
     *
     * @return a new {@code ProviderAccountRequest} instance based on the current builder state.
     */
    ProviderAccountRequest build();

}
