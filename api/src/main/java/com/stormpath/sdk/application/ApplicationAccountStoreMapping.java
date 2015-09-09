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
package com.stormpath.sdk.application;

/**
 * @since 1.0.RC4.6
 */
public interface ApplicationAccountStoreMapping extends AccountStoreMapping {

    /**
     * Returns the Application represented by this {@code AccountStoreMapping} resource.
     *
     * @return the Application represented by this {@code AccountStoreMapping} resource.
     */
    Application getApplication();

    /**
     * Sets the Application represented by this {@code AccountStoreMapping} resource.
     *
     * @param application the Application represented by this {@code AccountStoreMapping} resource.
     * @return this instance for method chaining.
     */
    AccountStoreMapping setApplication(Application application);

}
