/*
 * Copyright 2017 Stormpath, Inc.
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
package com.stormpath.sdk.oauth;

import com.stormpath.sdk.query.Options;

/**
 * @since 1.6.0
 */
public interface OAuthPolicyOptions<T> extends Options {

    /**
     * Ensures that when retrieving an OAuthPolicy, the OAuthPolicy's assigned {@link OAuthPolicy#getScopes()} scopes}
     * are also retrieved in the same request.  This enhances performance by leveraging a single request
     * to retrieve multiple related resources you know you will use.
     *
     * @return this instance for method chaining.
     */
    T withScopes();

    /**
     * Ensures that when retrieving an OAuthPolicy, the OAuthPolicy's assigned {@link OAuthPolicy#getScopes()} scopes}
     * are also retrieved in the same request.  This enhances performance by leveraging a single request
     * to retrieve multiple related resources you know you will use.
     *
     * @param limit defines the maximum quantity of items to be retrieved when expanding this Collection Resource. Min:1, Max: 100. The default value is 25.
     * @return this instance for method chaining.
     */
    T withScopes(int limit);

    /**
     * Ensures that when retrieving an OAuthPolicy, the OAuthPolicy's assigned {@link OAuthPolicy#getScopes()} scopes}
     * are also retrieved in the same request.  This enhances performance by leveraging a single request
     * to retrieve multiple related resources you know you will use.
     *
     * @param limit defines the maximum quantity of items to be retrieved when expanding this Collection Resource. Min:1, Max: 100. The default value is 25.
     * @param offset the zero-based starting index in the entire collection of the first item to return. Default is 0
     * @return this instance for method chaining.
     */
    T withScopes(int limit, int offset);
}
