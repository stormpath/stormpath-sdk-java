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
package com.stormpath.sdk.impl.http.authc;

import com.stormpath.sdk.authc.RequestAuthenticator;
import com.stormpath.sdk.client.ClientCredentials;
import com.stormpath.sdk.http.Request;
import com.stormpath.sdk.authc.RequestAuthenticationException;
import com.stormpath.sdk.impl.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * This {@link RequestAuthenticator} implements a <a href="http://docs.stormpath.com/rest/product-guide/#authentication-basic">HTTP
 * Basic Authentication</a> scheme. This defines the HTTP authentication scheme to be used when communicating with the Stormpath API server.
 * </pre>
 * The `Client` gets the request authenticator configured via
 * {@link com.stormpath.sdk.client.ClientBuilder#setAuthenticationScheme(com.stormpath.sdk.client.AuthenticationScheme)}
 *
 * @since 0.9.3
 */
public class BasicRequestAuthenticator implements RequestAuthenticator {
    private static final Logger log = LoggerFactory.getLogger(BasicRequestAuthenticator.class);

    public static final String STORMPATH_DATE_HEADER = "X-Stormpath-Date";
    public static final String AUTHENTICATION_SCHEME = "Basic";

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd'T'HHmmss'Z'";
    public static final String TIME_ZONE = "UTC";

    private static final String NL = "\n";

    @Override
    public void authenticate(Request request, ClientCredentials clientCredentials) throws RequestAuthenticationException {
        Date date = new Date();

        SimpleDateFormat timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
        timestampFormat.setTimeZone(new SimpleTimeZone(0, TIME_ZONE));
        String timestamp = timestampFormat.format(date);

        request.getHeaders().set(STORMPATH_DATE_HEADER, timestamp);

        String authorizationHeader = clientCredentials.getId() + ":" + clientCredentials.getSecret();
        byte[] valueBytes;
        try {
            valueBytes = authorizationHeader.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RequestAuthenticationException("Unable to acquire UTF-8 bytes!");
        }
        authorizationHeader = Base64.encodeBase64String(valueBytes);
        request.getHeaders().set(AUTHORIZATION_HEADER, AUTHENTICATION_SCHEME + " " + authorizationHeader);
    }

}
