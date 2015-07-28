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
package com.stormpath.sdk.tenant;

import com.stormpath.sdk.query.*;

/**
 * A {@link Tenant}-specific {@link Criteria} class, enabling a Tenant-specific
 * <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent</a>query DSL.
 * TenantCriteria instances can be constructed by using the {@link Tenants} utility class, for example:
 * <pre>
 * Tenants.options()
 *  .withApplications()
 *  .withDirectories()
 * </pre>
 *
 * @since 1.0.RC4.6
 */
public interface TenantCriteria extends Criteria<TenantCriteria>, TenantOptions<TenantCriteria> {

}
