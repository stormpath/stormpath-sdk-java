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
package com.stormpath.sdk.api;

/**
 * Authenticates an API HTTP Request and returns a {@link ApiAuthenticationResult result}.
 *
 * @see com.stormpath.sdk.application.Application#authenticateApiRequest(Object)
 * @see #execute()
 * @since 1.0.RC
 */
public interface ApiRequestAuthenticator {

    /**
     * Returns an {@link ApiAuthenticationResult ApiAuthenticationResult} after a successful authentication to an
     * HTTP API endpoint.
     *
     * <p>The concrete type of the authentication result will depend on the request type, and can be resolved to the
     * specific type using a {@link com.stormpath.sdk.authc.AuthenticationResultVisitor}.
     *
     * @return ApiAuthenticationResult if the API request was authenticated successfully.
     *
     * @see com.stormpath.sdk.application.Application#authenticateApiRequest(Object)
     * @see com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
     *
     * @deprecated this method will be removed soon. Use {@link ApiRequestAuthenticator#authenticate(Object)} instead
     */
    ApiAuthenticationResult execute();

    /**
     * Authenticates an HTTP request submitted to your application's API, returning a result that reflects the
     * successfully authenticated {@link com.stormpath.sdk.account.Account} that made the request and the {@link ApiKey} used to authenticate
     * the request.  Throws a {@link com.stormpath.sdk.resource.ResourceException} if the request cannot be authenticated.
     * <p>
     * This method will automatically authenticate <em>both</em> HTTP Basic and OAuth 2 requests.  However, if you
     * require more specific or customized OAuth request processing, use the
     * {@link com.stormpath.sdk.oauth.OauthRequestAuthenticator#authenticate(Object)} method instead; that method allows you to customize how an OAuth request
     * is processed.
     * For example, you will likely want to call {@link com.stormpath.sdk.oauth.OauthRequestAuthenticator#authenticate(Object)} for requests
     * directed to your application's specific OAuth 2 token and authorization urls (often referenced as
     * {@code /oauth2/token} and {@code /oauth2/authorize} in OAuth 2 documentation).
     * </p>
     * */
    ApiAuthenticationResult authenticate(Object httpRequest);
}
