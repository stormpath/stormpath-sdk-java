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
 * The possible locations in an HTTP request where an OAuth 2 bearer token may be found and used for authenticating
 * the request.  By default, the SDK will only inspect the {@link #HEADER} and {@link #BODY} during a request; using
 * request parameters for authentication is generally discouraged for security reasons.  That being said, if you have a
 * specific need for using request parameters, such as supporting a legacy HTTP client, you can configure the SDK to
 * check the {@link #QUERY_PARAM} location as well.  Example configuration below.
 *
 * <h3>Usage</h3>
 *
 * <p>When you accept a known OAuth HTTP request, you can specify the request locations that will be checked when
 * retrieving the OAuth Access Token used to authenticate the request.  For example:</p>
 *
 * <pre>
 * import static com.stormpath.sdk.oauth.RequestLocation.*;
 * ...
 *
 * application.authenticateOauth(httpRequest).<b>{@link OauthRequestAuthenticator#inLocation(RequestLocation...) inLocation}</b>(HEADER, BODY, QUERY_PARAM).execute();
 * </pre>
 *
 * <p>
 * Again, by default, the SDK will automatically inspect the {@link #HEADER} and {@link #BODY}.  You must explicitly
 * add {@link #QUERY_PARAM} if you wish to use query parameters for authentication, as query parameters are generally
 * not considered as secure as the other two locations.
 * </p>
 *
 * @since 1.0.RC
 * @see OauthRequestAuthenticator#inLocation(RequestLocation...)
 * @see ResourceRequestAuthenticator#inLocation(RequestLocation...)
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
     * <p>Unlike the {@link #HEADER} and {@link #BODY} locations, <b>this location is NOT
     * checked by default</b>, as query parameters are generally perceived as less secure than the other two
     * locations.  If you wish to also inspect request query parameters for authenticating the OAuth request, this
     * option must be configured explicitly, as shown in this enum's top-level JavaDoc.</p>
     */
    QUERY_PARAM
}
