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
package com.stormpath.sdk.tenant;

import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * @since 0.1
 */
public interface Tenant extends Resource, Saveable, TenantActions {

    /**
     * Returns the tenant's globally-unique name in Stormpath.
     * <p/>
     * <b>THIS CAN CHANGE IN THE FUTURE.  Do not rely on it as a permanent identifier.</b>  If you need a permanent ID,
     * use the {@link #getHref() href} as the permanent ID (this is true for all resources, not just Tenant resources).
     *
     * @return the tenant's Stormpath globally-unique name. THIS CAN CHANGE. Do not rely on it as a permanent
     *         identifier.
     */
    String getName();

    /**
     * Returns the tenant's globally-unique human-readable key in Stormpath.
     * <p/>
     * <b>THIS CAN CHANGE IN THE FUTURE.  Do not rely on it as a permanent identifier.</b>  If you need a permanent ID,
     * use the {@link #getHref() href} as the permanent ID (this is true for all resources, not just Tenant resources).
     *
     * @return the tenant's Stormpath globally-unique human-readable name key. THIS CAN CHANGE. Do not rely on it as a
     *         permanent identifier.
     */
    String getKey();

}
