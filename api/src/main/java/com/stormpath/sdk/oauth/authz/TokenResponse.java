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
package com.stormpath.sdk.oauth.authz;

/**
 * The TokenResponse is a wrapper for the Bearer result of the {@link com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationRequestBuilder Basic Authentication}.
 * It provides access to all the disaggregated information contained in the Bearer.
 *
 * @since 1.0.RC
 */
public interface TokenResponse {

    /**
     * Returns the <a href="http://self-issued.info/docs/draft-ietf-oauth-json-web-token.html">Json Web Token</a> of this Bearer.
     *
     * @return the JWT of this Bearer.
     */
    String getAccessToken();

    /**
     * Returns the space separated collection of scopes.
     *
     * @return the space separated collection of scopes.
     */
    String getScope();

    /**
     * Returns the type of the accessToken result. Currently only "Bearer" is returned.
     *
     * @return The type of the accessToken result. Currently only "Bearer" is returned.
     */
    String getTokenType();

    /**
     * Returns an string containing the time to live.
     *
     * @return an string containing the time to live.
     */
    String getExpiresIn();

    /**
     * <b>NOTE: Not yer supported.<b/>
     * <p/>
     * Returns the refresh token of this Bearer.
     *
     * @return the refresh token of this Bearer.
     */
    String getRefreshToken();

    /**
     * Returns all the non-values of this token response as json (body message).
     *
     * @return all the non-values of this token response as json (body message).
     */
    String toJson();

    /**
     * Returns the Application Href identifying the Application this Bearer corresponds to.
     *
     * @return the Application Href identifying the Application this Bearer corresponds to.
     */
    String getApplicationHref();
}
