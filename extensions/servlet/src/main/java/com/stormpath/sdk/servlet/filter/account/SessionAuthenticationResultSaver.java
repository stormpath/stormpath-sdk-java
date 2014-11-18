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
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashSet;
import java.util.Set;

public class SessionAuthenticationResultSaver implements Saver<AuthenticationResult>, ServletContextInitializable {

    private static final String ACCOUNT_SESSION_ATTRIBUTE_NAMES_PROP = "stormpath.web.account.session.attribute.names";

    private Set<String> sessionAttributeNames;

    @Override
    public void init(ServletContext servletContext) throws ServletException {
        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);

        String val = config.get(ACCOUNT_SESSION_ATTRIBUTE_NAMES_PROP);
        Assert.hasText(val, ACCOUNT_SESSION_ATTRIBUTE_NAMES_PROP + " value is required.");

        String[] logs = Strings.split(val);

        this.sessionAttributeNames = new LinkedHashSet<String>(Collections.toList(logs));
    }

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {

        if (result == null) {
            remove(request);
            return;
        }

        Account account = result.getAccount();

        HttpSession session = request.getSession();

        for(String name : this.sessionAttributeNames) {
            session.setAttribute(name, account);
        }
    }

    protected void remove(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            for(String name : this.sessionAttributeNames) {
                session.removeAttribute(name);
            }
        }
    }
}
