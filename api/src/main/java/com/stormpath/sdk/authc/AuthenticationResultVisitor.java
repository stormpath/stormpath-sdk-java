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
package com.stormpath.sdk.authc;

import com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationResult;
import com.stormpath.sdk.oauth.authc.OauthAuthenticationResult;

/**
 * A <a href="http://en.wikipedia.org/wiki/Visitor_pattern">Visitor design pattern</a> used to
 * construct {@link com.stormpath.sdk.api.ApiKey} instances.
 *
 * @since 1.0.RC
 */
public interface AuthenticationResultVisitor {

    /**
     * Visits the {@link AuthenticationResult} instance in order to construct the proper {@link com.stormpath.sdk.api.ApiKey}.
     *
     * @param result the concrete {@link AuthenticationResult} instance being visited.
     */
    void visit(AuthenticationResult result);

    /**
     * Visits the {@link ApiAuthenticationResult} instance in order to construct the proper {@link com.stormpath.sdk.api.ApiKey}.
     *
     * @param result the concrete {@link AuthenticationResult} instance being visited.
     */
    void visit(ApiAuthenticationResult result);

    /**
     * Visits the {@link OauthAuthenticationResult} instance in order to construct the proper {@link com.stormpath.sdk.api.ApiKey}.
     *
     * @param result the concrete {@link AuthenticationResult} instance being visited.
     */
    void visit(OauthAuthenticationResult result);

    /**
     * Visits the {@link BasicOauthAuthenticationResult} instance in order to construct the proper {@link com.stormpath.sdk.api.ApiKey}.
     *
     * @param result the concrete {@link AuthenticationResult} instance being visited.
     */
    void visit(BasicOauthAuthenticationResult result);
}
