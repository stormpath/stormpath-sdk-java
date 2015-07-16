/*
 * Copyright 2014 Stormpath, Inc.
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

import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Resource;

import java.util.Date;

/**
 * Resource containing Provider-specific data for the {@link com.stormpath.sdk.account.Account}. For example, for Google, it
 * contains <code>refreshToken</code> and <code>accessToken</code>.
 *
 * @since 1.0.beta
 */
public interface ProviderData extends Resource, Auditable {

    /**
     * Returns the ProviderData's created date.
     *
     * @return the ProviderData's created date.
     */
    Date getCreatedAt();

    /**
     * Returns the ProviderData's last modification date.
     *
     * @return the ProviderData's last modification date.
     */
    Date getModifiedAt();

    /**
     * Getter for the Stormapth ID of the Provider (e.g. "stormpath", "facebook" or "google").
     *
     * @return the Stormapth ID of the Provider (e.g. "stormpath", "facebook" or "google").
     */
    String getProviderId();

}
