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
package com.stormpath.sdk.resource;

import com.stormpath.sdk.directory.CustomData;

/**
 * Interface to be implemented by {@link Resource Resources} capable of storing {@link CustomData custom data}. For example:
 * {@link com.stormpath.sdk.account.Account Account}, {@link com.stormpath.sdk.application.Application Application},
 * {@link com.stormpath.sdk.directory.Directory Directory}, {@link com.stormpath.sdk.group.Group Group} and
 * {@link com.stormpath.sdk.tenant.Tenant Tenant}.
 *
 * @since 1.0.0
 */
public interface Extendable {

    /**
     * Returns the Stormpath CustomData owned by the resource implementing this interface.
     *
     * @return the Stormpath CustomData owned by the resource implementing this interface.
     */
    CustomData getCustomData();
}
