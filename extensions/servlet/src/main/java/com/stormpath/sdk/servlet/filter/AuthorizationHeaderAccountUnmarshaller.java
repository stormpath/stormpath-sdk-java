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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthorizationHeaderAccountUnmarshaller implements Unmarshaller<Account> {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationHeaderAccountUnmarshaller.class);

    public static final String AUTHENTICATION_RESULT_REQUEST_ATTRIBUTE_NAME = AuthenticationResult.class.getName();

    @Override
    public Account unmarshall(HttpServletRequest request, HttpServletResponse response) {
        request.getAuthType();

        String authzHeaderValue = request.getHeader("Authorization");

        if (authzHeaderValue != null) {

            Application app = ApplicationResolver.INSTANCE.getApplication(request.getServletContext());

            try {
                AuthenticationResult result = app.authenticateApiRequest(request);

                //store the result in the request so any downstream components that need to access it can
                //(so they don't have to authenticate the header again)
                request.setAttribute(AUTHENTICATION_RESULT_REQUEST_ATTRIBUTE_NAME, result);

                return result.getAccount();

            } catch (Exception e) {
                log.debug("Unable to resolve request account using the http authentication header.", e);
            }
        }

        return null;
    }
}
