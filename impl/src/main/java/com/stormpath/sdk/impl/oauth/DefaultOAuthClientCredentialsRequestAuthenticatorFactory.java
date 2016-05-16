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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.application.DefaultApplication;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthClientCredentialsRequestAuthenticatorFactory;

/**
 * @since 1.0.0
 */
public class DefaultOAuthClientCredentialsRequestAuthenticatorFactory implements OAuthClientCredentialsRequestAuthenticatorFactory {

    @Override
    public OAuthClientCredentialsGrantRequestAuthenticator forApplication(Application application) {
        return ((DefaultApplication) application).createClientCredentialsGrantAuthenticator();
    }
}
