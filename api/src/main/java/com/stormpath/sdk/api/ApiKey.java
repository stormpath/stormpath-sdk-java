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
package com.stormpath.sdk.api;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.tenant.Tenant;

/**
 * An ApiKey represents the api key that belongs to a particular {@link Account},
 * used for API authentication purposes.
 *
 * @since 1.1.beta
 */
public interface ApiKey extends Resource, Saveable, Deletable {

    /**
     * Returns the id of the api key.
     *
     * @return the id of the api key.
     */
    String getId();

    /**
     * Returns the secret of the api key.
     *
     * @return the secret of the api key.
     */
    String getSecret();

    /**
     * Returns the api key's status.  Api keys that are not {@link ApiKeyStatus#ENABLED ENABLED}
     * may not allow their @link Account}s to authenticate their requests.
     *
     * @return the api key's status.
     */
    ApiKeyStatus getStatus();

    /**
     * Sets the api key's status.  Api keys that are not {@link ApiKeyStatus#ENABLED ENABLED}
     * may not allow their @link Account}s to authenticate their requests.
     *
     * @param status the api key's status.
     */
    void setStatus(ApiKeyStatus status);

    /**
     * Returns the {@link Account} to which this ApiKey belongs.
     *
     * @return the {@link Account} to which this ApiKey belongs.
     */
    Account getAccount();

    /**
     * Returns the Stormpath {@link Tenant} that owns this ApiKey resource.
     *
     * @return the Stormpath {@link Tenant} that owns this ApiKey resource.
     */
    Tenant getTenant();

    /**
     * Saves this ApiKey resource and ensures the returned ApiKey response reflects the specified saveApiKeyRequest.
     *
     * @param request The {@link SaveApiKeyRequest} to use to customize the ApiKey resource returned in the save
     *                        response.
     */
    void save(SaveApiKeyRequest request);
}
