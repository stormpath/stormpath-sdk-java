/*
 * Copyright 2017 Stormpath, Inc.
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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.impl.query.DefaultOptions;
import com.stormpath.sdk.oauth.OAuthPolicyOptions;

/**
 * @since 1.6.0
 */
public class DefaultOAuthPolicyOptions extends DefaultOptions<OAuthPolicyOptions> implements OAuthPolicyOptions<OAuthPolicyOptions>{
    @Override
    public OAuthPolicyOptions withScopes() {
        return expand(DefaultOAuthPolicy.SCOPES);
    }

    @Override
    public OAuthPolicyOptions withScopes(int limit) {
        return expand(DefaultOAuthPolicy.SCOPES, limit);
    }

    @Override
    public OAuthPolicyOptions withScopes(int limit, int offset) {
        return expand(DefaultOAuthPolicy.SCOPES, limit, offset);
    }
}
