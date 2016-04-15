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
package com.stormpath.sdk.oauth;

import com.stormpath.sdk.api.ApiAuthenticationResult;

import java.util.Set;

/**
 * An {@code AuthenticationResult} that indicates a client authenticated with your server-side API (eg
 * REST API) via OAuth 2.  The ApiKey used to authenticate the request can be obtained via {@link #getApiKey()}.
 *
 * <h3>Scope</h3>
 *
 * <p>Any OAuth scope granted to the API client is available via {@link #getScope()}.  You can use the returned scope
 * values to perform permission checks in your application before allowing or denying a particular feature. Scope values
 * are application-specific and interpreted however you wish.</p>
 *
 * <p>Application-specific scope values may be assigned to an OAuth Access Token when the token is created.
 * Implement the {@link ScopeFactory ScopeFactory} interface and provide your
 * {@code ScopeFactory} instance to the {@code application.}{@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object) authenticateOauthRequest(request)}
 * method at the time a new token is being requested (typically when a client requests a new token via your
 * application's oauth token endpoint, e.g. {@code /oauth/token}).</p>
 *
 * @see com.stormpath.sdk.application.Application#authenticateOauthRequest(Object) application.authenticateOauthRequest(request)
 * @see AccessTokenResult
 * @since 1.0.RC
 */
public interface OAuthAuthenticationResult extends ApiAuthenticationResult {

    /**
     * Returns the set of scopes granted to the Oauth API caller.
     *
     * @return the set of scopes granted to the Oauth API caller.
     */
    Set<String> getScope();
}
