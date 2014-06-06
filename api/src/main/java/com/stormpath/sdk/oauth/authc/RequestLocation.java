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
package com.stormpath.sdk.oauth.authc;

/**
 * The possible locations in an HTTP request where an OAuth 2 bearer token may be found and used for authenticating
 * the request.  By default, all request locations are inspected, but you can explicitly restrict which locations are
 * inspected by specifying only the locations you want.
 *
 * <h3>Usage</h3>
 * When you accept a known OAuth HTTP request, you can tell the Stormpath SDK which request locations you are willing to
 * inspect when the SDK processes the request.  For example:
 * <pre>
 * import static com.stormpath.sdk.oauth.authc.RequestLocation.*;
 * ...
 *
 * application.authenticateOauth(httpRequest).<b>{@link OauthRequestAuthenticator#inLocation(RequestLocation...) inLocation}</b>(HEADER, BODY).execute();
 * </pre>
 * <p>
 * Again, by default, the SDK will automatically inspect all available locations.  But it might be valuable to only
 * inspect {@code HEADER} or {@code BODY} locations in higher security environments where query parameters may not be
 * seen as secure enough.
 * </p>
 *
 * @since 1.0.RC
 * @see OauthRequestAuthenticator#inLocation(RequestLocation...)
 * @see BearerOauthRequestAuthenticator#inLocation(RequestLocation...)
 */
public enum RequestLocation {

    /**
     * Try to find the OAuth 2 bearer token in the the HTTP request's {@code Authorization} header.  This is an option
     * defined in the <a href="http://tools.ietf.org/html/rfc6750#section-2.1">OAuth 2 Bearer Token
     * specification, Section 2.1</a>.
     */
    HEADER,

    /**
     * Try to find the OAuth 2 bearer token in the HTTP request's {@code application/x-www-form-urlencoded} body as a
     * {@code access_token} parameter.  This is an option defined in the
     * <a href="http://tools.ietf.org/html/rfc6750#section-2.2">OAuth 2 Bearer Token specification, Section 2.2</a>.
     */
    BODY,

    /**
     * Try to find the OAuth 2 bearer token in an HTTP request {@code access_token} query parameter.  This is an option
     * defined in the <a href="http://tools.ietf.org/html/rfc6750#section-2.3">OAuth 2 Bearer Token
     * specification, Section 2.3</a>.
     */
    QUERY_PARAM
}
