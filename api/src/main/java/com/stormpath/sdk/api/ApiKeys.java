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
package com.stormpath.sdk.api;

import com.stormpath.sdk.lang.Classes;

/**
 * Static utility/helper methods for working with {@link ApiKey} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * ApiKey-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. for example:
 * <pre>
 * <b>ApiKeys.criteria()</b>
 *     .offsetBy(50)
 *     .limitTo(25)
 *     .withTenant()
 *     .withAccount();
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.api.ApiKeys.*;
 *
 * ...
 *
 *  <b>criteria()</b>
 *     .offsetBy(50)
 *     .limitTo(25)
 *     .withTenant()
 *     .withAccount();
 * </pre>
 *
 * @since 1.0.RC
 */
public final class ApiKeys {

    /**
     * Returns a new {@link ApiKeyOptions} instance, used to customize how one or more {@link ApiKey}s are retrieved.
     *
     * @return a new {@link ApiKeyOptions} instance, used to customize how one or more {@link ApiKey}s are retrieved.
     */
    public static ApiKeyOptions<ApiKeyOptions> options() {
        return (ApiKeyOptions) Classes.newInstance("com.stormpath.sdk.impl.api.DefaultApiKeyOptions");
    }

    /**
     * Returns a new {@link ApiKeyCriteria} instance to use to formulate an ApiKey query.
     * <p/>
     * For example:
     * <pre>
     * ApiKeyCriteria criteria = ApiKeys.criteria()
     *     .offsetBy(50)
     *     .limitTo(25)
     *     .withTenant()
     *     .withAccount();
     * </pre>
     *
     * @return a new {@link ApiKeyCriteria} instance to use to formulate an ApiKey query.
     */
    public static ApiKeyCriteria criteria() {
        return (ApiKeyCriteria) Classes.newInstance("com.stormpath.sdk.impl.api.DefaultApiKeyCriteria");
    }
}
