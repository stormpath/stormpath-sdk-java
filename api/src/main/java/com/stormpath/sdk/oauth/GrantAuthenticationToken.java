/*
* Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.oauth;

import com.stormpath.sdk.resource.Resource;

/**
 * A GrantAuthenticationToken instance reflects the result of executing an Authentication Request to create or refresh an {@link AccessToken AccessToken}.
 *
 * @since 1.0.RC6
 */
public interface GrantAuthenticationToken extends Resource {

    /**
     * Returns the value denoting the access token for the create response, as a <a href="https://en.wikipedia.org/wiki/JSON_Web_Token">Json Web Token</a>.
     *
     * @return the String value denoting the access token for the create response, as a <a href="https://en.wikipedia.org/wiki/JSON_Web_Token">Json Web Token</a>.
     */
    public String getAccessToken();

    /**
     * Returns the value denoting the access token for the refresh response, as a <a href="https://en.wikipedia.org/wiki/JSON_Web_Token">Json Web Token</a>.
     *
     * @return the String value denoting the access token for the refresh response, as a <a href="https://en.wikipedia.org/wiki/JSON_Web_Token">Json Web Token</a>.
     */
    public String getRefreshToken();

    /**
     * Returns the type of the token included in the response.
     *
     * @return the String value denoting the type of the token included in the response.
     */
    public String getTokenType();

    /**
     * Returns the value denoting the time in seconds before the token expires.
     *
     * @return the String representation of the value denoting the time in seconds before the token expires.
     */
    public String getExpiresIn();

    /**
     * Returns the value denoting the href location of the token in Stormpath.
     *
     * @return the String value denoting the href location of the token in Stormpath.
     */
    public String getAccessTokenHref();

    /**
     * Return the token included in the response as a {@link AccessToken AccessToken} instance.
     *
     * @return the {@link AccessToken AccessToken} representation of the token included in the response.
     */
    public AccessToken getAsAccessToken();

    /**
     * Return the token included in the refresh response as a {@link RefreshToken RefreshToken} instance.
     *
     * @return the {@link RefreshToken RefreshToken} representation of the token included in the refresh response.
     */
    public RefreshToken getAsRefreshToken();
}
