/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.oauth;


import com.stormpath.sdk.lang.Classes;

/**
 * Date: 11/6/15
 */
public class Authenticators {

    private Authenticators() {
    }

    public static final PasswordGrantAuthenticatorFactory passwordGrantAuthenticator =
            (PasswordGrantAuthenticatorFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultPasswordGrantAuthenticatorFactory");

    public static final RefreshGrantAuthenticatorFactory refreshGrantAuthenticator =
            (RefreshGrantAuthenticatorFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultRefreshGrantAuthenticatorFactory");

    public static final JwtAuthenticatorFactory jwtAuthenticator =
            (JwtAuthenticatorFactory) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultJwtAuthenticatorFactory");

}

