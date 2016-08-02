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
package com.stormpath.sdk.client;

/**
 * Enumeration that defines the available HTTP authentication schemes to be used when communicating with the Stormpath API server.
 * </pre>
 * The Authentication Scheme setting is helpful in cases where the code is run in a platform where the header information for
 * outgoing HTTP requests is modified and thus causing communication issues. For example, for Google App Engine you
 * need to set {@link SAuthc1AuthenticationScheme#BASIC} in order for your code to properly communicate with Stormpath API server.
 * </pre>
 * There are currently two authentication schemes available: <a href="http://docs.stormpath.com/rest/product-guide/#authentication-basic">HTTP
 * Basic Authentication</a> and <a href="http://docs.stormpath.com/rest/product-guide/#authentication-digest">Digest Authentication</a>
 *
 * @since 0.9.3
 */
public class SAuthc1AuthenticationScheme implements AuthenticationScheme {

    @Override
    public String getRequestAuthenticatorClassName() {
        return "com.stormpath.sdk.impl.http.authc.SAuthc1RequestAuthenticator";
    }

}
