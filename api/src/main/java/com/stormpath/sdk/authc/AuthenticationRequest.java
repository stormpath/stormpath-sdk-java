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
package com.stormpath.sdk.authc;

import com.stormpath.sdk.directory.AccountStore;

/**
 * @since 0.1
 */
public interface AuthenticationRequest<P,C> {

    P getPrincipals();

    C getCredentials();

    String getHost();

    void clear();

    /**
     * Returns the specific account store this authentication request will be targeted to.
     * @return the specific account store this authentication request will be targeted to.
     * @since 1.0.alpha
     */
    AccountStore getAccountStore();

}
