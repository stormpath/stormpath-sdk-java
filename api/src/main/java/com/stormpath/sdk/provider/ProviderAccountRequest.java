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

/**
 * Represents an attempt to get or create a Provider-based {@link com.stormpath.sdk.account.Account} record in Stormpath.
 * <p/>
 * NOTE: A Provider-specific {@link com.stormpath.sdk.directory.Directory} must previously exist in Stormpath and it must also
 * be an Enabled Account Store within the Application.
 *
 * @see {@link com.stormpath.sdk.directory.CreateDirectoryRequestBuilder}
 * @since 1.0.beta
 */
public interface ProviderAccountRequest {

    /**
     * Getter for the {@link ProviderData} Resource containing the data required to access to the account.
     *
     * @return the {@link ProviderData} Resource containing the data required to access to the account.
     */
    ProviderData getProviderData();
}
