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
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.EqualsExpressionFactory;

import java.lang.reflect.Constructor;

/**
 * @since 1.1.beta
 */
public final class ApiKeys {

    @SuppressWarnings("unchecked")
    private static final Class<CreateApiKeyRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.account.DefaultCreateApiKeyRequestBuilder");

    /**
     * Returns a new {@link ApiKeyOptions} instance, used to customize how one or more {@link ApiKey}s are retrieved.
     *
     * @return a new {@link ApiKeyOptions} instance, used to customize how one or more {@link ApiKey}s are retrieved.
     */
    public static ApiKeyOptions<ApiKeyOptions> options() {
        return (ApiKeyOptions) Classes.newInstance("com.stormpath.sdk.impl.account.DefaultApiKeyOptions");
    }

    /**
     * Returns a new {@link ApiKeyCriteria} instance to use to formulate an ApiKey query.
     * <p/>
     * For example:
     * <pre>
     * ApiKeyCriteria criteria = ApiKeys.criteria();
     * EqualsExpressionFactory idExpressionFactory = ApiKeys.id();
     * Criterion idEquals = idExpressionFactory.eq("Sffwef345348nernfgierR");
     * criteria.add(idEquals);
     * </pre>
     *
     * @return a new {@link ApiKeyCriteria} instance to use to formulate an ApiKey query.
     */
    public static ApiKeyCriteria criteria() {
        return (ApiKeyCriteria) Classes.newInstance("com.stormpath.sdk.impl.account.DefaultApiKeyCriteria");
    }

    /**
     * Creates a new {@link ApiKeyCriteria} instance using the specified {@code criterion} as the first query condition.
     *
     * @return a new {@link ApiKeyCriteria} instance using the specified {@code criterion} as the first query condition.
     */
    public static ApiKeyCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    /**
     * Creates a new {@link EqualsExpressionFactory} instance reflecting the ApiKey {@link ApiKey#getId() id}
     * property, to be used to construct an id Criterion when building an {@link ApiKeyCriteria} query.  For example:
     * <pre>
     * ApiKeys.where(<b>ApiKeys.id()</b>.eq("Sffwef345348nernfgierR");
     * </pre>
     * The above example invokes the returned factory's <code>eq()</code> method.  This
     * produces a status-specific {@link Criterion} which is added to the criteria query (via the
     * {@link #where(com.stormpath.sdk.query.Criterion) where} method).  For example, the following code is equivalent:
     * <pre>
     * ApiKeyCriteria criteria = ApiKeys.criteria();
     * EqualsExpressionFactory idExpressionFactory = ApiKeys.id();
     * Criterion idEquals = idExpressionFactory.eq("Sffwef345348nernfgierR");
     * criteria.add(idEquals);
     * </pre>
     * The first code example is clearly more succinct and readable.
     *
     * @return a new {@link ApiKey#getId() id}-specific {@link EqualsExpressionFactory} instance, to be
     *         used to construct a criterion when building an {@link ApiKeyCriteria} query.
     */
    public static EqualsExpressionFactory id() {
        return newEqualsExpressionFactory("id");
    }

    /**
     * Creates a new {@link CreateApiKeyRequestBuilder CreateApiKeyRequestBuilder}. The builder can be used to customize any
     * creation response options as necessary.
     *
     * @return a new {@link CreateApiKeyRequestBuilder CreateApiKeyRequestBuilder}.
     *
     * @see com.stormpath.sdk.account.Account#createApiKey(CreateApiKeyRequest) Account#createApiKey(CreateApiKeyRequest)
     * @since 1.1.beta
     */
    public static CreateApiKeyRequestBuilder newCreateRequest() {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS);
        return (CreateApiKeyRequestBuilder) Classes.instantiate(ctor);
    }

    private static EqualsExpressionFactory newEqualsExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory";
        return (EqualsExpressionFactory) Classes.newInstance(FQCN, propName);
    }

}
