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
package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.api.ApiRequestAuthenticator;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.oauth.OauthRequestAuthenticator;

import java.util.HashSet;
import java.util.Set;

/**
 * @since 1.0.RC
 */
public class DefaultApiRequestAuthenticator implements ApiRequestAuthenticator {

    private static final ApiAuthenticationRequestFactory FACTORY = new ApiAuthenticationRequestFactory();

    private final Application application;

    private HttpRequest httpRequest;

    private static final String OAUTH_REQUEST_AUTHENTICATOR_FQCN =
            "com.stormpath.sdk.impl.oauth.authc.DefaultOauthRequestAuthenticator";

//    private static final String OAUTH_AUTHENTICATION_REQUEST_DISPATCHER_FQCN =
//            "com.stormpath.sdk.impl.oauth.authc.OauthAuthenticationRequestDispatcher";

    private static final String HTTP_SERVLET_REQUEST_FQCN = "javax.servlet.http.HttpServletRequest";

    private static final Set<Class> HTTP_REQUEST_SUPPORTED_CLASSES;

    private static final String HTTP_REQUEST_NOT_SUPPORTED_MSG =
            "Class [%s] is not one of the supported http requests classes [%s].";

    static {
        Set<Class> supportedClasses = new HashSet<Class>();

        supportedClasses.add(HttpRequest.class);

        if (Classes.isAvailable(HTTP_SERVLET_REQUEST_FQCN)) {
            supportedClasses.add(Classes.forName(HTTP_SERVLET_REQUEST_FQCN));
        }

        HTTP_REQUEST_SUPPORTED_CLASSES = supportedClasses;
    }


    public DefaultApiRequestAuthenticator(Application application, Object httpRequest) {

        Assert.notNull(application, "application argument cannot be null.");
        Assert.notNull(httpRequest, "httpRequest argument cannot be null.");
        this.application = application;

        if (HttpRequest.class.isAssignableFrom(httpRequest.getClass())) {
            this.httpRequest = (HttpRequest) httpRequest;
        } else {
            Assert.isTrue(Classes.isAvailable(HTTP_SERVLET_REQUEST_FQCN),
                          "The " + HTTP_SERVLET_REQUEST_FQCN + " class must be in the runtime classpath.");

            Assert.isInstanceOf(javax.servlet.http.HttpServletRequest.class, httpRequest,
                                "The specified httpRequest argument must be an instance of " +
            HttpRequest.class.getName() + " or " + HTTP_SERVLET_REQUEST_FQCN);

            javax.servlet.http.HttpServletRequest httpServletRequest =
                (javax.servlet.http.HttpServletRequest)httpRequest;

            this.httpRequest = new com.stormpath.sdk.impl.http.ServletHttpRequest(httpServletRequest);
        }
    }

    public DefaultApiRequestAuthenticator(Application application) {

        Assert.notNull(application, "application argument cannot be null.");
        this.application = application;

    }

    @Override
    public ApiAuthenticationResult execute() {
        AuthenticationRequest request = FACTORY.createFrom(httpRequest);
        AuthenticationResult result = application.authenticateAccount(request);
        Assert.isInstanceOf(ApiAuthenticationResult.class, result);
        return (ApiAuthenticationResult) result;
    }


    @Override
    public ApiAuthenticationResult authenticate(Object httpRequest) {

        Assert.notNull(httpRequest, "httpRequest argument cannot be null.");

        if (HttpRequest.class.isAssignableFrom(httpRequest.getClass())) {
            this.httpRequest = (HttpRequest) httpRequest;
        } else {
            Assert.isTrue(Classes.isAvailable(HTTP_SERVLET_REQUEST_FQCN),
                    "The " + HTTP_SERVLET_REQUEST_FQCN + " class must be in the runtime classpath.");

            Assert.isInstanceOf(javax.servlet.http.HttpServletRequest.class, httpRequest,
                    "The specified httpRequest argument must be an instance of " +
                            HttpRequest.class.getName() + " or " + HTTP_SERVLET_REQUEST_FQCN);

            javax.servlet.http.HttpServletRequest httpServletRequest =
                    (javax.servlet.http.HttpServletRequest)httpRequest;

            this.httpRequest = new com.stormpath.sdk.impl.http.ServletHttpRequest(httpServletRequest);
        }

        //api
        return new DefaultApiRequestAuthenticator(this.application, this.httpRequest).execute();
    }

    @SuppressWarnings("unchecked")
    private void validateHttpRequest(Object httpRequest) {
        Assert.notNull(httpRequest);
        Class httpRequestClass = httpRequest.getClass();
        for (Class supportedClass : HTTP_REQUEST_SUPPORTED_CLASSES) {
            if (supportedClass.isAssignableFrom(httpRequestClass)) {
                return;
            }
        }
        throw new IllegalArgumentException(String.format(HTTP_REQUEST_NOT_SUPPORTED_MSG, httpRequestClass.getName(),
                HTTP_REQUEST_SUPPORTED_CLASSES.toString()));
    }

}
