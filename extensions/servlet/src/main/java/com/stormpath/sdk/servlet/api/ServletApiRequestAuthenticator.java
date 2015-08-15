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
package com.stormpath.sdk.servlet.api;

import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC4.6
 */
public interface ServletApiRequestAuthenticator {

    /**
     * Authenticates an {@link HttpServletRequest} request submitted to your application's API, returning a result that reflects the
     * successfully authenticated {@link com.stormpath.sdk.account.Account} that made the request and the {@link com.stormpath.sdk.api.ApiKey} used to authenticate
     * the request.  Throws a {@link com.stormpath.sdk.resource.ResourceException} if the request cannot be authenticated.
     * <p>
     * This method will automatically authenticate <em>both</em> HTTP Basic and OAuth 2 requests.  However, if you
     * require more specific or customized OAuth request processing, use the
     * {@link com.stormpath.sdk.servlet.oauth.ServletOauthRequestAuthenticator#authenticate(javax.servlet.http.HttpServletRequest)} method instead; that method allows you to customize how an OAuth request
     * is processed.
     * For example, you will likely want to call {@link ServletOauthRequestAuthenticator#authenticate(HttpServletRequest)} for requests
     * directed to your application's specific OAuth 2 token and authorization urls (often referenced as
     * {@code /oauth2/token} and {@code /oauth2/authorize} in OAuth 2 documentation).
     *
     * <p>The concrete type of the authentication result will depend on the request type, and can be resolved to the
     * specific type using a {@link com.stormpath.sdk.authc.AuthenticationResultVisitor}.
     *
     * <h3>Basic Example</h3>
     *
     * //assume a request to, say, https://api.mycompany.com/foo:
     * public void onApiRequest(HttpServletRequest request, HttpServletResponse response) {
     *
     *    ApiAuthenticationResult result = Servlets.servletApiRequestAuthenticator(application).authenticate();
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
     *
     * <h3>OAuth 2 Example</h3>
     * <p>The above example is generic - it assumes either HTTP Basic or OAuth 2 authentication, and do not
     * distinguish between the two.  This is totally fine if that is suitable for your application.</p>
     *
     * <p>However, OAuth 2 also has the notion of <em>scopes</em>, also known as application-specific permissions.  If
     * the request is an OAuth 2 request, and you have {@link ServletApiRequestAuthenticator#authenticate(HttpRequest)}  previously assigned
     * scopes to OAuth tokens} you can check those scopes during an API request to control access.</p>
     * <p>So how do we do that?  How do we know if a request was a regular HTTP Basic request or an OAuth 2 request?
     *
     * We use an {@link com.stormpath.sdk.authc.AuthenticationResultVisitor AuthenticationResultVisitor}.  This will
     * allow us - in a compile-time/type-safe way to react to whatever is returned by the authenticate method.  For
     * example:</p>
     *
     * <pre>
     * //assume a request to, say, https://api.mycompany.com/foo:
     *
     * public void onApiRequest(HttpServletRequest /&#42; or your framework-specific request - see above &#42;/ request) {
     *
     *      ApiAuthenticationResult result = Servlets.servletApiRequestAuthenticator(application).authenticate(request);
     *
     *      final Set&lt;String&gt; scope = new LinkedHashSet&lt;String&gt;;
     *
     *      result.accept(new {@link com.stormpath.sdk.authc.AuthenticationResultVisitor AuthenticationResultVisitor}() {
     *
     *        &#64;Override
     *        public void visit(ApiAuthenticationResult result) {
     *            //the request was a normal HTTP Basic request
     *        }
     *
     *        &#64;Override
     *        public void visit(OauthAuthenticationResult result) {
     *            //the request was authenticated using OAuth
     *            //ensure we can use the scopes for access control checks after this visitor returns:
     *            scope.addAll(result.getScope());
     *        }
     *        ...
     *    });
     *
     *    Account account = result.getAccount();
     *
     *    // Check to see that account is allowed to make this request or not before processing the request:
     *    // check the <b>scope</b> here for any permissions that are required for this API call.  You can also check
     *    // the account's {@link com.stormpath.sdk.account.Account#getGroups() groups} or any of your own
     *    // application-specific permissions that might exist in the group's or account's {@link com.stormpath.sdk.account.Account#getCustomData() customData}.
     *
     *    //process request here
     * }
     * </pre>
     *
     *
     * @param httpServletRequest an {@code javax.servlet.http.HttpServletRequest} instance.
     * @return an ApiAuthenticationResult if the API request was authenticated successfully.
     *
     * @throws IllegalArgumentException if the method argument is null or is not a
     * <a href="http://docs.oracle.com/javaee/7/api/javax/servlet/ServletRequest.html">
     * {@code javax.servlet.http.HttpServletRequest}</a> instance.
     *
     * @see com.stormpath.sdk.servlet.oauth.ServletOauthRequestAuthenticator#authenticate(javax.servlet.http.HttpServletRequest)
     * @since 1.0.RC4.6
     */
     public ApiAuthenticationResult authenticate(HttpServletRequest httpServletRequest);

}
