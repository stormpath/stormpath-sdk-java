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
/**
 * Support for implementing a nearly hands-off OAuth 2 'resource server' and 'authorization server' in your
 * application.  This allows you to secure your own REST API using OAuth 2 with extremely little effort.
 *
 * <p><b>NOTE: Using the the Stormpath SDK to secure your API is currently intended only to support the use case of a
 * REST API client interacting with their own data directly via your REST API server.</b>  This use case is sometimes
 * called &quot;two-legged OAuth&quot; because there are only two parties involved: the API client and your API server
 * (with the Stomrpath SDK embedded in it).  The Stormpath SDK does not currently support '3 party' OAuth where there
 * is an API client (1st party) that is attempting to access a different end-user's (2nd party) information from your
 * API server (3rd party).  If you want to see support for 3-party use cases, please let us know by contacting us at
 * {@code support@stormpath.com}.  We prioritize new features based on customer requests.</p>
 *
 * <h2>Usage</h2>
 *
 * <p>SDK users will basically have in their REST API at a minimum:
 * <ul>
 *     <li>An OAuth Token creation endpoint, for example, {@code /oauth/token}</li>
 *     <li>One or more application-specific REST resource endpoints that are secured via OAuth, for example,
 *     {@code /videos/1234}</li>
 * </ul>
 *
 * <h3>Access Token Endpoint</h3>
 *
 * <p>When an OAuth 2 client communicates with your REST API, they will use an {@link com.stormpath.sdk.api.ApiKey
 * ApiKey} associated with their {@link com.stormpath.sdk.account.Account Account} to make an
 * <a href="http://tools.ietf.org/html/rfc6749#section-3.2">Client Credentials Grant Type</a> request (using HTTP Basic
 * authentication) to your token creation endpoint.  You relay this request to the Stormpath SDK via your
 * {@link com.stormpath.sdk.application.Application Application} instance, and the SDK will
 * <em>automatically</em>:
 * <ol>
 *     <li>Authenticate the request, performing an ApiKey authentication check, and then</li>
 *     <li>Generate a new Access Token that you use as your HTTP response.</li>
 * </ol>
 * </p>
 *
 * <p>For example:</p>
 *
 * <pre>
 * //assume a POST request to, say, https://api.mycompany.com/oauth/token:
 *
 * public void processOauthTokenRequest(HttpServletRequest request, HttpServletResponse response) {
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
 * <p><b>Note:</b> you do not need to use the {@code HttpServletRequest} API.  A translation {@code HttpRequest} builder
 * is available.  See {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * application.authenticateOauthRequest(httpRequest)} for more information.</p>
 *
 * <h3>Resource Endpoint</h3>
 *
 * <p>After your REST client has authenticated with your token endpoint, they will send subsequent requests to your
 * other REST resource endpoints using the previously generated access token.  Just relay the request to the Stormpath
 * SDK, and the SDK will automatically:</p>
 *
 * <ol>
 *     <li>Authenticate the request using the discovered OAuth authentication token</li>
 *     <li>Return the associated {@link com.stormpath.sdk.account.Account Account} so you know who is invoking your
 *         REST API.  You can check the account, it's {@link com.stormpath.sdk.account.Account#getGroups() groups}, or
 *         its {@link com.stormpath.sdk.account.Account#getCustomData() customData} to perform access control checks to
 *         see if they are allowed (authorized) to invoke that endpoint or not.</li>
 *     <li>Return any OAuth {@link com.stormpath.sdk.oauth.TokenResponse#getScope() scope} assigned to the client's
 *         access token.  You can use OAuth scope values for access control checks as well.</li>
 * </ol>
 *
 * <p>For example:</p>
 *
 * <pre>
 * //assume a request to, say, https://api.mycompany.com/videos/1234:
 *
 * public void onApiRequest(HttpServletRequest request, HttpServletResponse response) {
 *
 *    Application application = client.getResource(myApplicationRestUrl, Application.class);
 *
 *    ApiAuthenticationResult result = application.authenticateApiRequest(request).execute();
 *
 *    Account account = result.getAccount();
 *
 *    // Check to see that account is allowed to make this request or not before processing
 *    // the request.  For example, by checking the account's {@link com.stormpath.sdk.account.Account#getGroups() groups} or any of your own
 *    // application-specific permissions that might exist in the group's or account's {@link com.stormpath.sdk.account.Account#getCustomData() customData}.
 *    assertAuthorized(account); //implement the 'assertAuthorized' method yourself.
 *
 *    //process request here
 * }
 * </pre>
 *
 * <p><b>Note:</b> Again, you do not need to use the {@code HttpServletRequest} API.  A translation {@code HttpRequest}
 * builder is available for those that do not depend on the {@code Servlet} API.  See
 * {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * application.authenticateOauthRequest(httpRequest)} for more information.</p>
 *
 * <h2>More Information</h2>
 *
 * <p>The {@link com.stormpath.sdk.application.Application#authenticateApiRequest(Object)
 * application.authenticateApiRequest(httpRequest)} and
 * {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * application.authenticateOauthRequest(httpRequest) application.authenticateOauthRequest(httpRequest)} JavaDoc has
 * much more information and sample use cases.  Please see those methods' JavaDoc for more.</p>
 *
 * @see com.stormpath.sdk.application.Application Application
 * @see com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * @see com.stormpath.sdk.application.Application#authenticateApiRequest(Object)
 * @since 1.0.RC
 */