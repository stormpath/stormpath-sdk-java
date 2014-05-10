/*
 *
 *  * Copyright 2014 Stormpath, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.stormpath.sdk.client;

/**
 * Static utility/helper class for working with {@link ApiKey} resources. For example:
 * <pre>
 * <b>ApiKeys.builder()</b>
 *     .setFileLocation(path)
 *     .build();
 * </pre>
 *
 * @since 1.0.beta
 * @deprecated in 1.0.RC and will be removed before 1.0 final. Use {@link com.stormpath.sdk.api.ApiKeys} instead.
 */
@Deprecated
public class ApiKeys {

    /**
     * Returns a new {@link ApiKeyBuilder} instance, used to construct {@link ApiKey} instances.
     *
     * @return a new {@link ApiKeyBuilder} instance, used to construct {@link ApiKey} instances.
     * @deprecated in 1.0.RC and will be removed before 1.0 final. Use {@link com.stormpath.sdk.api.ApiKeys#builder()} instead.
     */
    @Deprecated
    public static ApiKeyBuilder builder() {
        return com.stormpath.sdk.api.ApiKeys.builder();
    }

}
