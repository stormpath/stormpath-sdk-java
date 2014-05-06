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
 * Placeholder for all the information pertaining to a Provider when attempting to create a new Provider-based {@link com.stormpath.sdk.directory.Directory} in Stormpath.
 *
 * @see CreateProviderRequestBuilder
 * @see com.stormpath.sdk.directory.CreateDirectoryRequestBuilder#forProvider(CreateProviderRequest)
 * @since 1.0.beta
 */
public interface CreateProviderRequest {

    /**
     * Returns the Provider instance containing all the Provider information to be used when creating a Provider-based directory in Stormpath.
     *
     * @return the Provider instance containing all the Provider information to be used when creating a Provider-based directory in Stormpath.
     */
    Provider getProvider();

}
