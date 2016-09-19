/*
* Copyright 2016 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticationBuilder;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticationFactory;

/**
 * @since 1.1.0
 */
public class DefaultOAuthClientCredentialsGrantRequestAuthenticationFactory implements OAuthClientCredentialsGrantRequestAuthenticationFactory {

    @Override
    public OAuthClientCredentialsGrantRequestAuthenticationBuilder builder() {
        return (OAuthClientCredentialsGrantRequestAuthenticationBuilder) Classes.newInstance("com.stormpath.sdk.impl.oauth.DefaultOAuthClientCredentialsGrantRequestAuthenticationBuilder");
    }
}
