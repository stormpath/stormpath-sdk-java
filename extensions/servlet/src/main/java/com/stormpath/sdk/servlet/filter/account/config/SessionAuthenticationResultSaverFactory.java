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
package com.stormpath.sdk.servlet.filter.account.config;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.account.SessionAuthenticationResultSaver;
import com.stormpath.sdk.servlet.http.Saver;

import javax.servlet.ServletContext;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @since 1.0.RC3
 */
public class SessionAuthenticationResultSaverFactory
    extends ConfigSingletonFactory<Saver<AuthenticationResult>> {

    private static final String ACCOUNT_SESSION_ATTRIBUTE_NAMES_PROP = "stormpath.web.account.session.attribute.names";

    @Override
    protected Saver<AuthenticationResult> createInstance(ServletContext servletContext) throws Exception {

        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);

        String val = config.get(ACCOUNT_SESSION_ATTRIBUTE_NAMES_PROP);
        Assert.hasText(val, ACCOUNT_SESSION_ATTRIBUTE_NAMES_PROP + " value is required.");

        String[] logs = Strings.split(val);

        Set<String> sessionAttributeNames = new LinkedHashSet<String>(Collections.toList(logs));

        return new SessionAuthenticationResultSaver(sessionAttributeNames);
    }
}
