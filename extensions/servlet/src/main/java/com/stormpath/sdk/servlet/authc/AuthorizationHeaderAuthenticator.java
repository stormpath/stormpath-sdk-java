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
package com.stormpath.sdk.servlet.authc;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.application.DefaultApplicationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AuthorizationHeaderAuthenticator implements HttpAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationHeaderAuthenticator.class);

    /**
     * HTTP Authentication header, equal to <code>WWW-Authenticate</code>
     */
    private static final String AUTHENTICATE_HEADER = "WWW-Authenticate";

    private static final String AUTHORIZATION = "Authorization";

    private final ApplicationResolver applicationResolver = new DefaultApplicationResolver();

    private final AuthorizationHeaderParser parser = new DefaultAuthorizationHeaderParser();

    private final Map<String, HttpAuthenticationScheme> schemes; //supported schemes - iteration order retained

    public AuthorizationHeaderAuthenticator(List<HttpAuthenticationScheme> schemes) {

        Assert.notEmpty(schemes, "AuthenticationScheme list cannot be null or empty.");

        this.schemes = new LinkedHashMap<String, HttpAuthenticationScheme>(schemes.size());

        for (HttpAuthenticationScheme scheme : schemes) {
            this.schemes.put(scheme.getName().toLowerCase(), scheme);
        }
    }

    @Override
    public HttpAuthenticationResult authenticate(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {

        //spec says there can only be one value, no need to check for multiple values:

        String headerValue = request.getHeader(AUTHORIZATION);

        HttpCredentials credentials = parser.parse(headerValue);

        String schemeName = Strings.clean(credentials.getSchemeName());

        if (schemeName != null) {

            HttpAuthenticationScheme scheme = this.schemes.get(credentials.getSchemeName().toLowerCase());

            String schemeValue = credentials.getSchemeValue();

            if (schemeValue != null) {

                HttpAuthenticationAttempt attempt =
                    new DefaultHttpAuthenticationAttempt(request, response, credentials);

                try {
                    return scheme.authenticate(attempt);
                } catch (Throwable t) {
                    //DO NOT log the scheme value - it could contain sensitive information (e.g. Basic authc) -
                    //log the scheme name only.
                    String msg = "Unable to authenticate request with authentication scheme '" + schemeName + "': " +
                                 t.getMessage() + "  Sending HTTP challenge response.";
                    log.debug(msg, t);
                }
            }
        }

        sendChallenge(request, response);
        return new DefaultHttpAuthenticationResult(request, response, null, false);
    }

    protected void sendChallenge(HttpServletRequest request, HttpServletResponse response) {

        Application application = applicationResolver.getApplication(request.getServletContext());

        String appName = application.getName();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        for (HttpAuthenticationScheme scheme : this.schemes.values()) {
            String authcHeader = createWwwAuthenticateHeaderValue(scheme, appName);
            response.addHeader(AUTHENTICATE_HEADER, authcHeader);
        }
    }

    protected String createWwwAuthenticateHeaderValue(HttpAuthenticationScheme scheme, String appName) {
        return scheme.getName() + " realm=\"" + appName + "\"";
    }
}
