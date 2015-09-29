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
package com.stormpath.sdk.authc;

/**
 * @since 1.0.RC5
 */
public interface OauthGrantAuthenticationResult {

//Specification methods:
//    result.getAccessTokenString(); //String JWT
//    result.getRefreshTokenString(); //String JWT can be null
//    result.getAccessTokenHref();  //String



    /**
     * Returns the Access Token string that should be used by the client as the bearer token for subsequent requests.
     *
     * @return the Access Token string that should be used by the client as the bearer token for subsequent requests.
     */
    String getAccessToken();

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
    String getAccessTokenHref();

}
