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
package com.stormpath.sdk.servlet.filter.account.config;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.account.AuthenticationResultSaver;
import com.stormpath.sdk.servlet.http.Saver;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AuthenticationResultSaverFactory extends ConfigSingletonFactory<AuthenticationResultSaver> {

    public static final String ACCOUNT_SAVER_LOCATIONS = "stormpath.servlet.filter.authc.saver.savers";
    public static final String ACCOUNT_SAVER_PROPERTY_PREFIX = "stormpath.servlet.filter.authc.saver.savers.";

    @Override
    protected AuthenticationResultSaver createInstance(ServletContext servletContext) throws Exception {

        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);

        List<String> locations = null;
        String val = config.get(ACCOUNT_SAVER_LOCATIONS);
        if (Strings.hasText(val)) {
            String[] locs = Strings.split(val);
            locations = Arrays.asList(locs);
        }

        Assert.notEmpty(locations, "At least one " + ACCOUNT_SAVER_LOCATIONS + " value must be specified.");
        assert locations != null;

        Map<String, Saver> saverMap = config.getInstances(ACCOUNT_SAVER_PROPERTY_PREFIX, Saver.class);

        List<Saver<AuthenticationResult>> savers = new ArrayList<Saver<AuthenticationResult>>(saverMap.size());

        for (String location : locations) {

            Saver resolver = saverMap.get(location);
            Assert.notNull(resolver, "There is no configured AuthenticationResult Saver named " + location);

            Saver<AuthenticationResult> accountResolver = (Saver<AuthenticationResult>) resolver;
            savers.add(accountResolver);
        }

        return new AuthenticationResultSaver(savers);
    }
}
