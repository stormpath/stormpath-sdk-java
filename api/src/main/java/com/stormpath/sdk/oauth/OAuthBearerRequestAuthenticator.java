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

/**
 * This class is used to authenticate a Json Web Token against Stormpath. For example:
 * <pre>
 * Application app = obtainApplication();
 * JwtAuthenticationRequest authRequest = OAuthRequests.OAUTH_BEARER_REQUEST
 *      .builder()
 *      .setJwt(jwt)
 *      .build();
 * JwtAuthenticationResult result = Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(authRequest);
 * </pre>
 * This validation is always performed against Stormpath server, if you want to validate the token locally, simply apply
 * the {@link #withLocalValidation()} when performing the authentication. Like this:
 * <pre>
 * JwtAuthenticationResult result = Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(app).withLocalValidation().authenticate(authRequest);
 * </pre>
 *
 * @since 1.0.RC7
 */
public interface OAuthBearerRequestAuthenticator extends OAuthRequestAuthenticator<OAuthBearerRequestAuthenticationResult> {

    /**
     * Flags the authenticator to carry out a local validation rather than a validation against Stormpath's backend.
     * <p>Doing a local validation will for sure be faster since there is no network traffic involved. However, using Stormpath
     * to validate the token through the REST API ensures that the token can actually be validated against the state of your application
     * and account. To illustrate the difference:
     * </p>
     * <table summary="JWT validation">
     *   <col align="left">
     *   <col align="center">
     *   <col align="center">
     *   <tr>
     *      <td>Validation Criteria</td><td align="center">Locally</td><td align="center">Stormpath</td>
     *   <tr/>
     *   <tr>
     *      <td>Token hasn’t been tampered with</td><td align="center">yes</td><td align="center">yes</td>
     *   </tr>
     *   <tr>
     *      <td>Token hasn’t expired</td><td align="center">yes</td><td align="center">yes</td>
     *   </tr>
     *   <tr>
     *      <td>Token hasn’t been revoked</td><td align="center">no</td><td align="center">yes</td>
     *   </tr>
     *   <tr>
     *      <td>Account hasn’t been disabled, and hasn’t been deleted</td><td align="center">no</td><td align="center">yes</td>
     *   </tr>
     *   <tr>
     *      <td>Issuer is Stormpath</td><td align="center">yes</td><td align="center">yes</td>
     *   </tr>
     *   <tr>
     *      <td>Issuing application is still enabled, and hasn’t been deleted</td><td align="center">no</td><td align="center">yes</td>
     *   </tr>
     *   <tr>
     *      <td>Account is still in an account store for the issuing application</td><td align="center">no</td><td align="center">yes</td>
     *   </tr>
     * </table>

     * @return This instance for method chaining.
     */
    OAuthBearerRequestAuthenticator withLocalValidation();

}
