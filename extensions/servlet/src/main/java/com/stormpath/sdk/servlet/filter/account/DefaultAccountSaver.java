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

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.impl.DefaultConfig;
import com.stormpath.sdk.servlet.http.Mutator;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class DefaultAccountSaver implements Mutator<AuthenticationResult>, ServletContextInitializable {

    private Config config;
    private Mutator<AuthenticationResult> accountCookieMutator;

    @Override
    public void init(ServletContext servletContext) throws ServletException {
        this.config = ConfigResolver.INSTANCE.getConfig(servletContext);
        this.accountCookieMutator = createAccountCookieMutator(servletContext);
    }

    protected Mutator<AuthenticationResult> createAccountCookieMutator(ServletContext servletContext)
        throws ServletException {
        AccountCookieMutator mutator = new AccountCookieMutator();
        mutator.init(servletContext);
        return mutator;
    }

    public Config getConfig() {
        return this.config;
    }

    public Mutator<AuthenticationResult> getAccountCookieMutator() {
        return accountCookieMutator;
    }

    protected List<String> getAccountSaverLocations() {
        return this.config.getAccountSaverLocations();
    }

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {

        List<String> locations = getAccountSaverLocations();

        if (locations.contains("disabled")) {
            return;
        }

        for (String location : locations) {
            if ("cookie".equalsIgnoreCase(location)) {
                getAccountCookieMutator().set(request, response, result);
            } else if ("session".equalsIgnoreCase(location)) {
                HttpSession session = request.getSession();
                session.setAttribute("account", result.getAccount());
            } else {
                String msg = "Unrecognized " + DefaultConfig.ACCOUNT_SAVER_LOCATIONS + " config value: " + location;
                throw new IllegalArgumentException(msg);
            }
        }
    }
}
