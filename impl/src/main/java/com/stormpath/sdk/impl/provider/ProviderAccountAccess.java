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
package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.provider.ProviderData;
import com.stormpath.sdk.resource.Resource;

/**
 * Holds the information required to execute a Provider-based Account access attempt.
 *
 * @since 1.0.beta
 */
public interface ProviderAccountAccess<T extends ProviderData> extends Resource {

    /**
     * Returns the Provider-specific {@link ProviderData} containing the information required to execute an access attempt.
     *
     * @return the Provider-specific {@link ProviderData} containing the information required to execute an access attempt.
     */
    T getProviderData();

    /**
     * Sets the Provider-specific {@link ProviderData} containing the information required to execute an access attempt.
     *
     * @param providerData the Provider-specific {@link ProviderData} containing the information required to execute an access attempt.
     */
    void setProviderData(T providerData);

}
