/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.directory.Directory;

public class DefaultAccountStoreModel implements AccountStoreModel {

    private final Directory directory;
    private final ProviderModel providerModel;

    public DefaultAccountStoreModel(Directory directory, ProviderModel provider) {
        this.directory = directory;
        this.providerModel = provider;
    }

    @Override
    public String getHref() {
        return this.directory.getHref();
    }

    @Override
    public String getName() {
        return this.directory.getName();
    }

    @Override
    public ProviderModel getProvider() {
        return this.providerModel;
    }
}