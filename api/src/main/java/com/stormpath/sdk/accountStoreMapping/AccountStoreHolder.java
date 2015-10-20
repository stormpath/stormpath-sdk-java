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
package com.stormpath.sdk.accountStoreMapping;

/**
 * Interface to be implemented by {@link com.stormpath.sdk.resource.Resource Resources} capable of storing {@link com.stormpath.sdk.directory.AccountStore account stores}. For example:
 * {@link com.stormpath.sdk.application.Application Applications} and {@link com.stormpath.sdk.organization.Organization Organizations}.
 *
 * @since 1.0.RC5.1
 */
public interface AccountStoreHolder<T extends AccountStoreHolder> {
}
