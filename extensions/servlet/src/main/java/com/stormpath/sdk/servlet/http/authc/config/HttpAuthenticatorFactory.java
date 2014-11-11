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
package com.stormpath.sdk.servlet.http.authc.config;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.Factory;
import com.stormpath.sdk.servlet.config.ImplementationClassResolver;
import com.stormpath.sdk.servlet.http.authc.AuthorizationHeaderAuthenticator;
import com.stormpath.sdk.servlet.http.authc.BasicAuthenticationScheme;
import com.stormpath.sdk.servlet.http.authc.BearerAuthenticationScheme;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticationScheme;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticator;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpAuthenticatorFactory implements Factory<HttpAuthenticator>, ServletContextInitializable {

    public static final String CHALLENGE_PROPERTY_NAME = "stormpath.servlet.http.authc.challenge";
    public static final String SCHEME_PROPERTY_NAME_PREFIX = "stormpath.servlet.http.authc.scheme.";

    private HttpAuthenticator authenticator;

    @Override
    public void init(ServletContext servletContext) throws ServletException {

        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);

        boolean challenge = Boolean.parseBoolean(config.get(CHALLENGE_PROPERTY_NAME));

        Map<String,Class<? extends HttpAuthenticationScheme>> configuredSchemes =
            new LinkedHashMap<String, Class<? extends HttpAuthenticationScheme>>();

        //add defaults:
        configuredSchemes.put("basic", BasicAuthenticationScheme.class);
        configuredSchemes.put("bearer", BearerAuthenticationScheme.class);

        //find any configured ones (which may or may not override the defaults):
        Map<String,Class<HttpAuthenticationScheme>> foundClasses =
            new ImplementationClassResolver<HttpAuthenticationScheme>(
                config, SCHEME_PROPERTY_NAME_PREFIX, HttpAuthenticationScheme.class)
            .findImplementationClasses();

        if (!com.stormpath.sdk.lang.Collections.isEmpty(foundClasses)) {
            configuredSchemes.putAll(foundClasses);
        }

        List<HttpAuthenticationScheme> schemes = new ArrayList<HttpAuthenticationScheme>(configuredSchemes.size());

        for(Class<? extends HttpAuthenticationScheme> schemeClass : configuredSchemes.values()) {
            HttpAuthenticationScheme scheme = Classes.newInstance(schemeClass);
            if (scheme instanceof ServletContextInitializable) {
                ((ServletContextInitializable)scheme).init(servletContext);
            }
            schemes.add(scheme);
        }

        this.authenticator = new AuthorizationHeaderAuthenticator(schemes, challenge);
    }

    @Override
    public HttpAuthenticator getInstance() {
        return this.authenticator;
    }
}
