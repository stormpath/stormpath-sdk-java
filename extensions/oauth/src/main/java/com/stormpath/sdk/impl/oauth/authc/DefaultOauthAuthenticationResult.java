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
package com.stormpath.sdk.impl.oauth.authc;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.impl.authc.DefaultApiAuthenticationResult;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.oauth.authc.OauthAuthenticationResult;

import java.util.Set;

/**
 * @since 1.0.RC
 */
public class DefaultOauthAuthenticationResult extends DefaultApiAuthenticationResult implements OauthAuthenticationResult {

    private final Set<String> scope;

    public DefaultOauthAuthenticationResult(InternalDataStore dataStore, ApiKey apiKey, Set<String> scope) {
        super(dataStore, apiKey);
        this.scope = scope;
    }

    @Override
    public Set<String> getScope() {
        return scope;
    }

    @Override
    public void accept(AuthenticationResultVisitor visitor) {
        visitor.visit(this);
    }
}
