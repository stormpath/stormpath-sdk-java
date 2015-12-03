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
package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.application.ApplicationAccountStoreMappingCriteria;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * @since 0.9
 */
public class DefaultApplicationAccountStoreMappingCriteria extends DefaultCriteria<ApplicationAccountStoreMappingCriteria, ApplicationAccountStoreMappingOptions> implements ApplicationAccountStoreMappingCriteria {

    public DefaultApplicationAccountStoreMappingCriteria() {
        super(new DefaultApplicationAccountStoreMappingOptions());
    }

    @Override
    public ApplicationAccountStoreMappingCriteria orderByListIndex() {
        return orderBy(DefaultApplicationAccountStoreMapping.LIST_INDEX);
    }

    @Override
    public ApplicationAccountStoreMappingCriteria withApplication() {
        getOptions().withApplication();
        return this;
    }

    @Override
    public ApplicationAccountStoreMappingCriteria withAccountStore() {
        getOptions().withAccountStore();
        return this;
    }
}
