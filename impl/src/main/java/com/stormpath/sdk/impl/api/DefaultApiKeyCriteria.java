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
package com.stormpath.sdk.impl.api;

import com.stormpath.sdk.api.ApiKeyCriteria;
import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;
import com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory;
import com.stormpath.sdk.impl.security.DefaultSaltGenerator;
import com.stormpath.sdk.impl.security.SaltGenerator;

import static com.stormpath.sdk.impl.api.ApiKeyParameter.*;

/**
 * @since 1.1.beta
 */
public class DefaultApiKeyCriteria extends DefaultCriteria<ApiKeyCriteria, ApiKeyOptions> implements ApiKeyCriteria  {

    private final SaltGenerator saltGenerator;

    public DefaultApiKeyCriteria() {
        super(new DefaultApiKeyOptions());
        saltGenerator = new DefaultSaltGenerator();
        addDefaultCriterions();
    }

    @Override
    public ApiKeyCriteria withTenant() {
        getOptions().withTenant();
        return this;
    }

    @Override
    public ApiKeyCriteria withAccount() {
        getOptions().withAccount();
        return this;
    }

    protected void addDefaultCriterions() {

        add(new DefaultEqualsExpressionFactory(ENCRYPT_SECRET.getName()).eq(Boolean.TRUE));
        add(new DefaultEqualsExpressionFactory(ENCRYPTION_KEY_SIZE.getName()).eq(128));
        add(new DefaultEqualsExpressionFactory(ENCRYPTION_KEY_ITERATIONS.getName()).eq(1024));
        add(new DefaultEqualsExpressionFactory(ENCRYPTION_KEY_SALT.getName()).eq(saltGenerator.generate()));
    }
}
