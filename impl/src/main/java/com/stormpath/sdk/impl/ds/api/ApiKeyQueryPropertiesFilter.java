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
package com.stormpath.sdk.impl.ds.api;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyList;
import com.stormpath.sdk.impl.api.DefaultApiKeyCriteria;
import com.stormpath.sdk.impl.ds.QueryPropertiesFilter;
import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.impl.http.QueryStringFactory;
import com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory;
import com.stormpath.sdk.impl.security.DefaultSaltGenerator;
import com.stormpath.sdk.impl.security.SaltGenerator;

import java.util.Map;

import static com.stormpath.sdk.impl.api.ApiKeyParameter.*;

/**
 * @since 1.1.beta
 */
public class ApiKeyQueryPropertiesFilter implements QueryPropertiesFilter {

    private final SaltGenerator saltGenerator;
    private final QueryStringFactory queryStringFactory;

    public ApiKeyQueryPropertiesFilter() {
        saltGenerator = new DefaultSaltGenerator();
        queryStringFactory = new QueryStringFactory();
    }

    /**
     * <p>
     *    This method verifies the provided {@code queryString} and, if the {@code clazz} argument
     *    is of type {@link ApiKey} or {@link ApiKeyList}, returns new {@link QueryString} if no encryption
     *    parameters are found.
     * </p>
     * <p>
     *     The new {@link QueryString} will contain all the necessary parameters to encrypt the api key secret
     *     plus the parameters that are already present in the {@code queryString} argument.
     * </p>
     * <p>
     *     If the filter does not apply to api keys, or the {@code queryString} argument already has the encryption parameters,
     *     the same {@code queryString} argument will be returned.
     * </p>
     * @param clazz the {@link Class} to verify if this filter should run.
     * @param queryString the query string to be filtered.
     * @return a new {@link QueryString} with all the necessary parameters to encrypt the api key secret
     *     plus the parameters that are already present in the {@code queryString} argument, if the filter
     *     applies to api keys and the {@code queryString} argument does not have the encryption parameters;
     *     otherwise, the same {@code queryString} argument will be returned.
     */
    @Override
    public QueryString filter(Class clazz, QueryString queryString) {

        if ((ApiKey.class.isAssignableFrom(clazz) || ApiKeyList.class.isAssignableFrom(clazz))
             && (queryString == null || !queryString.containsKey(ENCRYPT_SECRET.getName()))) {

            DefaultApiKeyCriteria criteria = new DefaultApiKeyCriteria();
            criteria.add(new DefaultEqualsExpressionFactory(ENCRYPT_SECRET.getName()).eq(Boolean.TRUE));
            criteria.add(new DefaultEqualsExpressionFactory(ENCRYPTION_KEY_SIZE.getName()).eq(128));
            criteria.add(new DefaultEqualsExpressionFactory(ENCRYPTION_KEY_ITERATIONS.getName()).eq(1024));
            criteria.add(new DefaultEqualsExpressionFactory(ENCRYPTION_KEY_SALT.getName()).eq(saltGenerator.generate()));

            QueryString qs = queryStringFactory.createQueryString(criteria);

            if (queryString != null && !queryString.isEmpty()) {

                for (Map.Entry<String, String> entry : queryString.entrySet()) {

                    if (!entry.getValue().equals(ENCRYPT_SECRET.getName())
                            && !entry.getValue().equals(ENCRYPTION_KEY_SIZE.getName())
                            && !entry.getValue().equals(ENCRYPTION_KEY_ITERATIONS.getName())
                            && !entry.getValue().equals(ENCRYPTION_KEY_SALT.getName()))  {

                        qs.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            return qs;
        }

        return queryString;
    }
}
