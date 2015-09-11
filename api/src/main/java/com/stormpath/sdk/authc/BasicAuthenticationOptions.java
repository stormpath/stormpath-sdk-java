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
package com.stormpath.sdk.authc;

/**
 * BasicAuthentication-specific options that may be specified during authentication.
 *
 * @since 1.0.RC5
 */
public interface BasicAuthenticationOptions<T extends BasicAuthenticationOptions> extends AuthenticationOptions<T> {

    /**
     * Ensures that when retrieving the {@link AuthenticationResult}, the {@link com.stormpath.sdk.account.Account Account} is also
     * retrieved in the same request. This enhances performance by leveraging a single request to retrieve related resources you know you will use.
     *
     * @return this instance for method chaining.
     */
    T withAccount();

}
