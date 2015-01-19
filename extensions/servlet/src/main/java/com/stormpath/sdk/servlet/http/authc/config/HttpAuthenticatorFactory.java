/*
 * Copyright 2015 Stormpath, Inc.
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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.http.authc.AuthorizationHeaderAuthenticator;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticationScheme;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticator;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC3
 */
public class HttpAuthenticatorFactory extends ConfigSingletonFactory<HttpAuthenticator> {

    public static final String EVENT_PUBLISHER = "stormpath.web.request.event.publisher";
    public static final String CHALLENGE_PROPERTY_NAME = "stormpath.web.http.authc.challenge";
    public static final String SCHEMES_PROP = "stormpath.web.http.authc.schemes";
    public static final String SCHEME_PROPERTY_NAME_PREFIX = SCHEMES_PROP + ".";

    @Override
    protected HttpAuthenticator createInstance(ServletContext servletContext) throws Exception {
        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);

        List<String> schemeNames = null;
        String val = config.get(SCHEMES_PROP);
        if (Strings.hasText(val)) {
            String[] locs = Strings.split(val);
            schemeNames = Arrays.asList(locs);
        }

        Assert.notEmpty(schemeNames, "At least one " + SCHEMES_PROP + " value must be specified.");
        assert schemeNames != null;

        Map<String, HttpAuthenticationScheme> schemeMap =
            config.getInstances(SCHEME_PROPERTY_NAME_PREFIX, HttpAuthenticationScheme.class);

        List<HttpAuthenticationScheme> schemes = new ArrayList<HttpAuthenticationScheme>(schemeMap.size());

        for (String schemeName : schemeNames) {

            HttpAuthenticationScheme scheme = schemeMap.get(schemeName);
            Assert.notNull(scheme, "There is no configured HttpAuthenticationScheme named " + schemeName);
            schemes.add(scheme);
        }

        boolean challenge = Boolean.parseBoolean(config.get(CHALLENGE_PROPERTY_NAME));

        Publisher<RequestEvent> publisher = config.getInstance(EVENT_PUBLISHER);

        return new AuthorizationHeaderAuthenticator(schemes, challenge, publisher);
    }
}
