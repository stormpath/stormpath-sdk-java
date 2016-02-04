/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.oauth;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.tenant.Tenant;

/**
 * This is a base interface for those methods that Stormpath
 * {@link AccessToken}s and {@link RefreshToken}s have in common.
 *
 * @see AccessToken
 * @see RefreshToken
 *
 * @since 1.0.RC8.3
 */
public interface BaseOauth2Token extends Resource, Deletable {
    /**
     * Returns the String representation of the Json Web Token.
     *
     * @return a String value denoting a JWT.
     */
    String getJwt();

    /**
     * Returns the {@link Account} associated to this {@link AccessToken}.
     *
     * @return the {@link Account} associated to this {@link AccessToken}.
     */
    Account getAccount();

    /**
     * Returns the {@link Application} associated to this {@link AccessToken}.
     *
     * @return the {@link Application} associated to this {@link AccessToken}.
     */
    Application getApplication();

    /**
     * Returns the {@link Tenant} this {@link AccessToken} belongs to.
     *
     * @return the {@link Tenant} this {@link AccessToken} belongs to.
     */
    Tenant getTenant();
}
