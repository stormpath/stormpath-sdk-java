/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;

/**
 * Executes the actual attempt to access a Provider-based Account.
 *
 * @since 1.0.beta
 */
public class ProviderAccountResolver {

    private InternalDataStore dataStore;

    public ProviderAccountResolver(InternalDataStore dataStore) {
        Assert.notNull(dataStore, "dataStore cannot be null");
        this.dataStore = dataStore;
    }

    public ProviderAccountResult resolveProviderAccount(String parentHref, ProviderAccountRequest request) {
        Assert.notNull(parentHref, "parentHref argument must be specified");
        Assert.notNull(request, "request argument cannot be null");
        Assert.notNull(request.getProviderData(), "request's providerData must be specified");

        ProviderAccountAccess providerAccountAccess = new DefaultProviderAccountAccess(this.dataStore);
        providerAccountAccess.setProviderData(request.getProviderData());
        String href = parentHref + "/accounts";

        ProviderAccountResultHelper providerAccountResultHelper = this.dataStore.create(href, providerAccountAccess, ProviderAccountResultHelper.class);

        return providerAccountResultHelper.getProviderAccountResult();
    }

}
