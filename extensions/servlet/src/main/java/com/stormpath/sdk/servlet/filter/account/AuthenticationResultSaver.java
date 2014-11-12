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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AuthenticationResultSaver implements Saver<AuthenticationResult>, ServletContextInitializable {

    public static final String ACCOUNT_SAVER_LOCATIONS = "stormpath.servlet.filter.authc.saver.savers";
    public static final String ACCOUNT_SAVER_PROPERTY_PREFIX = "stormpath.servlet.filter.authc.saver.savers.";

    private List<Saver<AuthenticationResult>> savers;

    @SuppressWarnings("unchecked")
    @Override
    public void init(ServletContext servletContext) throws ServletException {
        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);

        List<String> locations = null;
        String val = config.get(ACCOUNT_SAVER_LOCATIONS);
        if (Strings.hasText(val)) {
            String[] locs = Strings.split(val);
            locations = Arrays.asList(locs);
        }

        Assert.notEmpty(locations, "At least one " + ACCOUNT_SAVER_LOCATIONS + " value must be specified.");
        assert locations != null;

        Map<String,Saver> saverMap = config.getInstances(ACCOUNT_SAVER_PROPERTY_PREFIX, Saver.class);

        List<Saver<AuthenticationResult>> savers = new ArrayList<Saver<AuthenticationResult>>(saverMap.size());

        for(String location : locations) {

            Saver resolver = saverMap.get(location);
            Assert.notNull(resolver, "There is no configured AuthenticationResult Saver named " + location);

            Saver<AuthenticationResult> accountResolver = (Saver<AuthenticationResult>)resolver;
            savers.add(accountResolver);
        }

        this.savers = Collections.unmodifiableList(savers);
    }

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        for(Saver<AuthenticationResult> saver : savers) {
            saver.set(request, response, result);
        }

        Account account = result.getAccount();
        //store under both names - can be convenient depending on how it is accessed:
        request.setAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME, account);
        request.setAttribute(Account.class.getName(), account);
    }
}
