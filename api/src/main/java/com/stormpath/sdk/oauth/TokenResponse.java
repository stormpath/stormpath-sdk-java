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

/**
 * Response data to be returned to an OAuth client as a result of processing a successful Client Password
 * Authentication Request as defined in the
 * <a href="http://tools.ietf.org/html/rfc6749#section-2.3.1">OAuth 2 specification, Section 2.3.1</a>.
 *
 * <h3>Usage</h3>
 *
 * <p>When you delegate an HTTP OAuth 2 Token Request to the Stormpath SDK, if the request is successful, a
 * {@code TokenResponse} will be returned.  This {@code TokenResponse} data must be sent to the OAuth client in the
 * HTTP response.  For example:</p>
 *
 * <pre>
 * //assume a POST request to, say, https://api.mycompany.com/oauth/token:
 *
 * public void processOAuthTokenRequest(HttpServletRequest request, HttpServletResponse response) {
 *
 *    Application application = client.getResource(myApplicationRestUrl, Application.class);
 *
 *    AccessTokenResult result = (AccessTokenResult) application.authenticateOauthRequest(request).execute();
 *
 *    <b>TokenResponse token = result.getTokenResponse();
 *
 *    response.setStatus(HttpServletResponse.SC_OK);
 *    response.setContentType("application/json");
 *    response.getWriter().print(token.toJson());</b>
 *
 *    response.getWriter().flush();
 * }
 * </pre>
 *
 * <p>As you can see, {@link #toJson()} will return a JSON string to populate the response body - it is not strictly
 * necessary to read individual properties on the {@code TokenResponse} instance.</p>
 *
 * @since 1.0.RC
 */
public interface TokenResponse {

    /**
     * Returns the Access Token string that should be used by the client as the bearer token for subsequent requests.
     *
     * @return the Access Token string that should be used by the client as the bearer token for subsequent requests.
     */
    String getAccessToken();

    /**
     * Returns the Id Token string that should be used by the client as defined in the OpenID Connect spec.
     * @return the Id Token string that should be used by the client as defined in the OpenID Connect spec.
     * @since 1.4.0
     */
    String getIdToken();

    /**
     * Returns the space separated collection of granted scopes.
     *
     * @return the space separated collection of granted scopes.
     */
    String getScope();

    /**
     * Returns the type of the accessToken result. Currently only "Bearer" is returned.
     *
     * @return The type of the accessToken result. Currently only "Bearer" is returned.
     */
    String getTokenType();

    /**
     * Returns the Time-To-Live value that indicates for how long the access token is valid.  After this amount of time
     * passes, the token cannot be used.
     *
     * @return the Time-To-Live value that indicates for how long the access token is valid.
     */
    String getExpiresIn();

    /**
     * <b>NOTE: Not yet supported.<b/>
     *
     * <p>Returns the refresh token of this Bearer.</p>
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
