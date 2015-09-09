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
package com.stormpath.sdk.organization;

import com.stormpath.sdk.directory.AccountStoreOptions;

/**
 * Organization-specific options that may be specified when retrieving {@link Organization} resources.
 *
 * @since 1.0.RC4.6
 */
public interface OrganizationOptions<T extends OrganizationOptions> extends AccountStoreOptions<T> {

    /**
     * Ensures that when retrieving an Organization, the Organization's {@link Organization#getCustomData() customData} is also
     * retrieved in the same request. This enhances performance by leveraging a single request to retrieve multiple
     * related resources you know you will use.
     *
     * @return this instance for method chaining.
     */
    T withCustomData();

}
