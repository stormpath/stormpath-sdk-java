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

import com.stormpath.sdk.api.ApiRequestAuthenticator;
import com.stormpath.sdk.http.HttpRequest;

/**
 * An OAuth-specific {@code ApiRequestAuthenticator} that implements the
 * <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> to allow customization of how
 * the authentication attempt is processed.  For example:
 *
 * <pre>
 * AuthenticationResult result = {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * application.authenticateOauthRequest(httpRequest)}
 *     <b>{@link #using(ScopeFactory) .using(scopeFactory)}
 *     {@link #withTtl(long) .withTtl(3600)}
 *     {@link #execute() .execute()};</b>
 * </pre>
 *
 * @see com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * @see #execute()
 * @since 1.0.RC
 */
public interface OAuthApiRequestAuthenticator extends ApiRequestAuthenticator {

    /**
     * Specifies the {@link ScopeFactory} to be used when generating a new Access Token as a result of authenticating
     * the OAuth request.
     *
     * <p><b>This method should only be called when the OAuth client is specifically requesting a new Access Token</b>,
     * for example, a request to your application's oauth token endpoint, e.g. {@code /oauth/token}</p>
     *
     * @param scopeFactory the {@link ScopeFactory} to be used for this authentication request.
     * @return a new {@link AccessTokenRequestAuthenticator} instance created with the current state of the
     *         this builder.
     */
    AccessTokenRequestAuthenticator using(ScopeFactory scopeFactory);

    /**
     * Specifies the <a href="http://en.wikipedia.org/wiki/Time_to_live">time to live</a> of this authentication request
     * in seconds.  If not specified, the default value is {@code 3600} (seconds) - i.e. 1 hour.
     *
     * <p><b>This method should only be called when the OAuth client is specifically requesting a new Access Token</b>,
     * for example, a request to your application's oauth token endpoint, e.g. {@code /oauth/token}</p>
     *
     * @param ttl the time to live (in seconds) of this authentication request.
     * @return a new {@link AccessTokenRequestAuthenticator} instance created with the current state of the
     *         this builder.
     */
    AccessTokenRequestAuthenticator withTtl(long ttl);

    /**
     * Specifies the request location(s) that will be checked when looking up the request's Access Token.  Unspecified
     * locations will not be checked.
     * <p>
     * If this method is not called, both the request header and body will be checked by default.
     * </p>
     *
     * <p>This method will return a new {@link ResourceRequestAuthenticator} with the current state of the this
     * builder.</p>
     *
     * @param locations the location(s) for the <code>Bearer</code>.
     * @return a new {@link ResourceRequestAuthenticator} instance created with the current state of the
     *         this builder.
     */
    ResourceRequestAuthenticator inLocation(RequestLocation... locations);

    /**
     * Executes this authentication request.
     *
     * @return the result of the authentication request in the form of a {@link OAuthAuthenticationResult}.
     * @deprecated this method will be removed soon. Use {@link OAuthApiRequestAuthenticator#authenticate(HttpRequest)} instead
     */
    @Deprecated
    OAuthAuthenticationResult execute();

    /**
     * Authenticates an OAuth-based HTTP request submitted to your application's API, returning a result that
     * reflects the successfully authenticated {@link com.stormpath.sdk.account.Account} that made the request and the {@link com.stormpath.sdk.api.ApiKey} used to
     * authenticate the request.  Throws a {@link com.stormpath.sdk.resource.ResourceException} if the request cannot be authenticated.
     *
     * <p>This method is only useful if you know for sure the HTTP request is an Oauth-based request, and:
     *
     * <ul>
     *     <li>
     *     The request is authenticating with an Access Token and you want to explicitly control the
     *     locations in the request where you allow the access token to exist.  If you're comfortable with the default
     *     behavior of inspecting the headers and request body (and not request params, as they can be seen as a less
     *     secure way of authentication), you do not need to call this method, and should call the
     *     ServletApi method instead.
     *     </li>
     *     <li>
     *     <p>The HTTP request is an OAuth Client Credentials Grant Type request whereby the client is explicitly
     *     asking for a new Access Token <em>and</em> you want to control the returned token's OAuth scope and/or
     *     time-to-live (TTL).</p>
     *     <p>This almost always is the case when the client is interacting with your
     *     OAuth token endpoint, for example, a URI like {@code /oauth2/tokens}. If either the request is a normal OAuth
     *     request or the above condition does not apply to you, then you do not need to call this method, and
     *     should call the {@link ApiRequestAuthenticator#authenticate(HttpRequest)} method instead.</p>
     *     </li>
     * </ul>
     * <p>Again, if either of these two scenarios above does not apply to your use case, do not call this method; call
     * the {@link ApiRequestAuthenticator#authenticate(HttpRequest)} method instead.</p>
     *
     * <p>Next, we'll cover these 2 scenarios.</p>
     *
     * <h3>Scenario 1: OAuth (Bearer) Access Token Allowed Locations</h3>
     *
     * <p>By default, this method and {@link ApiRequestAuthenticator#authenticate(HttpRequest)} will authenticate an OAuth request
     * that presents its (bearer) Access Token in two of three locations in the request:
     * <ol>
     *     <li>
     *         The request's {@code Authorization} header, per the
     *         <a href="http://tools.ietf.org/html/rfc6750#section-2.1">OAuth 2 Bearer Token specification, Section
     *         2.1</a>.
     *     </li>
     *     <li>
     *         The request {@code application/x-www-form-urlencoded} body as a {@code access_token} parameter, per the
     *         <a href="http://tools.ietf.org/html/rfc6750#section-2.2">OAuth 2 Bearer Token specification, Section
     *         2.2</a>
     *     </li>
     * </ol>
     * </p>
     * <p>
     * Although checking a request {@code access_token} query parameter, per the
     * <a href="http://tools.ietf.org/html/rfc6750#section-2.3">OAuth 2 Bearer Token specification, Section 2.3</a> is
     * also supported, query parameters are <em>NOT</em> inspected by default.  Using request parameters for
     * authentication is often seen as a potential security risk and generally discouraged.  That being said, if you
     * need to support this location, perhaps because you need to support a legacy client, you can enable this
     * location explicitly if desired, for example:
     * <pre>
     * import static com.stormpath.sdk.oauth.RequestLocation.*;
     *
     * Application application = client.getResource(myApplicationRestUrl, Application.class);
     *
     * OAuthAuthenticationResult result = Applications.oauthRequestAuthenticator(application)
     *     <b>{@link OAuthApiRequestAuthenticator#inLocation(com.stormpath.sdk.oauth.RequestLocation...) .inLocation(}{@link com.stormpath.sdk.oauth.RequestLocation#HEADER HEADER}, {@link com.stormpath.sdk.oauth.RequestLocation#BODY BODY}, {@link com.stormpath.sdk.oauth.RequestLocation#QUERY_PARAM QUERY_PARAM})</b>
     *     .authenticate(httpRequest);
     * </pre>
     * </p>
     *
     * <p>The above code example implies that you will tell developers which options they
     * may use (and may not use) when configuring their OAuth client to communicate with your API.</p>
     *
     * <h3>Scenario 2: Creating OAuth Access Tokens</h3>
     *
     * <p>If the HTTP request is sent to your OAuth token creation endpoint, for example, {@code /oauth2/token} you
     * will need to call this method, and the Stormpath SDK will automatically create an Access Token for you.  After
     * it is created, you must send the token to the client in the HTTP response.  For example:
     *
     * <pre>
     * //assume a POST request to, say, https://api.mycompany.com/oauth/token:
     *
     * public void processOauthTokenRequest(HttpServletRequest request, HttpServletResponse response) {
     *
     *    Application application = client.getResource(myApplicationRestUrl, Application.class);
     *
     *    AccessTokenResult result = (AccessTokenResult) Applications.oauthRequestAuthenticator(application).authenticate(request);
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
     * </p>
     *
     * <p>As you can see, {@link com.stormpath.sdk.oauth.TokenResponse#toJson() tokenResponse.toJson()} method
     * will return a JSON string to populate the response body - it is not strictly
     * necessary to read individual properties on the {@code TokenResponse} instance.</p>
     *
     * <h4>Non Servlet Environments</h4>
     *
     * <p>If your application does not run in a Servlet environment - for example, maybe you use a custom HTTP
     * framework, or Netty, or Play!, you can use the {@link com.stormpath.sdk.http.HttpRequestBuilder
     * HttpRequestBuilder} to represent your framework-specific HTTP request object as a type the Stormpath SDK
     * understands.  For example:</p>
     * <pre>
     * ...
     * <b>// Convert the framework-specific HTTP Request into a request type the Stormpath SDK understands:
     *    {@link com.stormpath.sdk.http.HttpRequest HttpRequest} request = {@link com.stormpath.sdk.http.HttpRequests HttpRequests}.method(frameworkSpecificRequest.getMethod())
     *        .headers(frameworkSpecificRequest.getHeaders())
     *        .queryParameters(frameworkSpecificRequest.getQueryParameters())
     *        .build();</b>
     *
     *    ApiAuthenticationResult result = Applications.oauthRequestAuthenticator(application).authenticate(request);
     * ...
     * </pre>
     *
     * <h4>Customizing the OAuth Access Token Time-To-Live (TTL)</h4>
     *
     * <p>By default, this SDK creates Access Tokens that are valid for 3600 seconds (1 hour).  If you want to change
     * this value, you will need to invoke the {@code withTtl} method on the returned executor and specify your desired
     * TTL.  For example:
     *
     * <pre>
     * //assume a POST request to, say, https://api.mycompany.com/oauth/token:
     *
     * public void processOauthTokenRequest(HttpServletRequest request, HttpServletResponse response) {
     *
     *    Application application = client.getResource(myApplicationRestUrl, Application.class);
     *
     *    <b>int desiredTimeoutSeconds = 3600; //change to your preferred value</b>
     *
     *    AccessTokenResult result = (AccessTokenResult) Applications.oauthRequestAuthenticator(application)
     *      .withTtl(desiredTimeoutSeconds)
     *      .authenticate(request);
     *
     *    TokenResponse token = result.getTokenResponse();
     *
     *    response.setStatus(HttpServletResponse.SC_OK);
     *    response.setContentType("application/json");
     *    response.getWriter().print(token.toJson());
     *
     *    response.getWriter().flush();
     * }
     * </pre>
     * </p>
     *
     * <h4>Customizing the OAuth Access Token Scope</h4>
     *
     * <p>As an Authorization protocol, OAuth allows you to attach <em>scope</em>, aka application-specific
     * <em>permissions</em> to an Access Token when it is created.  You can check this scope on
     * {@link ApiRequestAuthenticator#authenticate(HttpRequest)}  later requests} and make authorization decisions to allow or deny the API
     * request based on the granted scope.</p>
     *
     * <p>When an access token is created, you can specify your application's own custom scope by calling the
     * {@code withScope} method on the returned executor.  For example:
     *
     * <pre>
     * //assume a POST request to, say, https://api.mycompany.com/oauth/token:
     *
     * public void processOauthTokenRequest(HttpServletRequest request, HttpServletResponse response) {
     *
     *    Application application = client.getResource(myApplicationRestUrl, Application.class);
     *
     *    int desiredTimeoutSeconds = 3600; //change to your preferred value
     *
     *    <b>ScopeFactory scopeFactory = getScopeFactory(); //get your ScopeFactory implementation from your app config</b>
     *
     *    AccessTokenResult result = (AccessTokenResult)application
     *        .authenticateOauthRequest(request)
     *        .withTtl(desiredTimeoutSeconds)
     *        <b>.withScopeFactory(scopeFactory)</b>
     *        .authenticate(request);
     *
     *    TokenResponse token = result.getTokenResponse();
     *
     *    response.setStatus(HttpServletResponse.SC_OK);
     *    response.setContentType("application/json");
     *    response.getWriter().print(token.toJson());
     *
     *    response.getWriter().flush();
     * }
     * </pre>
     * </p>
     *
     * <p>Your {@link com.stormpath.sdk.oauth.ScopeFactory ScopeFactory} implementation can inspect the
     * 1) successfully authenticated API client Account and 2) the client's <em>requested</em> scope.  Your
     * implementation returns the <em>actual</em> scope that you want granted to the Access Token (which may or may not
     * be different than the requested scope based on your requirements).</p>
     *
     * @param httpRequest a {@link com.stormpath.sdk.http.HttpRequest} instance.
     * @return a new {@link OAuthAuthenticationResult} if the API request was authenticated successfully.
     * @throws IllegalArgumentException if the method argument is null or is not either a {@link com.stormpath.sdk.http.HttpRequest} instance.
     * @see ApiRequestAuthenticator#authenticate(com.stormpath.sdk.http.HttpRequest)
     *
     * @since 1.0.RC4.6
     */
    OAuthAuthenticationResult authenticate(HttpRequest httpRequest);

}
