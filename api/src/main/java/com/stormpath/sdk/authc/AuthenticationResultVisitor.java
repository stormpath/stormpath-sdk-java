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
 * AuthenticationResultVisitor
 *
 * @since 1.0.RC
 */
public interface AuthenticationResultVisitor {

    void visit(AuthenticationResult result);

    void visit(ApiAuthenticationResult result);

    void visit(OauthAuthenticationResult result);

    void visit(BasicOauthAuthenticationResult result);
}
