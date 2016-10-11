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
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.OAuthProvider;

import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.0
 */
public class DefaultOAuthProviderModel extends DefaultProviderModel implements OAuthProviderModel {

    private final String clientId;
    private final List<String> scope;

    public DefaultOAuthProviderModel(OAuthProvider provider) {
        super(provider);
        this.clientId = provider.getClientId();
        Assert.hasText(this.clientId, "provider clientId cannot be null or empty.");
        this.scope = Collections.unmodifiableList(provider.getScope());
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    @Override
    public List<String> getScope() {
        return scope;
    }


}
