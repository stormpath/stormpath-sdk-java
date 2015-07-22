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
package com.stormpath.sdk.authc;

import com.stormpath.sdk.account.CreateAccountRequestBuilder;
import com.stormpath.sdk.lang.Classes;

/**
 * Static utility/helper methods serving Authentication-related operations. For example, to construct a {@link com.stormpath.sdk.authc.BasicAuthenticationOptions BasicAuthentication Options}:
 *
 * <pre>
 * AuthenticationOptions options = Authentications.BASIC.options().withAccount();
 * AuthenticationResult result = app.authenticateAccount(request, options);
 * </pre>
 *
 * @since 1.0.RC4.6
 */
public final class Authentications {

    public static final BasicAuthenticationOptionsFactory BASIC = new BasicAuthenticationOptionsFactory();

    private static class BasicAuthenticationOptionsFactory {

        @SuppressWarnings("unchecked")
        private static final Class<CreateAccountRequestBuilder> BUILDER_CLASS =
                Classes.forName("com.stormpath.sdk.impl.authc.DefaultBasicAuthenticationOptions");

        /**
         * Returns a new {@link com.stormpath.sdk.authc.BasicAuthenticationOptions} instance, used to customize how the {@link com.stormpath.sdk.authc.AuthenticationResult AuthenticationResult}s are retrieved.
         *
         * @return a new {@link com.stormpath.sdk.authc.BasicAuthenticationOptions} instance, used to customize how the {@link com.stormpath.sdk.authc.AuthenticationResult AuthenticationResult}s are retrieved.
         */
        @SuppressWarnings("unchecked")
        public static BasicAuthenticationOptions<BasicAuthenticationOptions> options() {
            return (BasicAuthenticationOptions) Classes.newInstance(BUILDER_CLASS);
        }
    }

}
