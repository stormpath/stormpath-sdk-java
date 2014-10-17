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
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.util.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class VerifyFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(VerifyFilter.class);

    /**
     * Returns the context-relative URL where a user can be redirected to login.
     *
     * @return the context-relative URL where a user can be redirected to login.
     */
    public String getVerifyUrl() {
        return getConfig().getVerifyUrl();
    }

    public String getVerifyNextUrl() {
        return getConfig().getVerifyNextUrl();
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {

        String method = request.getMethod();

        if ("GET".equalsIgnoreCase(method)) {
            doGet(request, response);
        } else {
            ServletUtils.issueRedirect(request, response, getVerifyUrl(), null, true, true);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        String sptoken = Strings.clean(request.getParameter("sptoken"));

        if (sptoken == null) {
            String url =
                getConfig().getLogoutUrl(); //safest thing to do if token is not specified (could be illegal access)
            ServletUtils.issueRedirect(request, response, url, null, true, true);
            return;
        }

        try {
            verify(request, response, sptoken);
        } catch (Exception e) {
            //TODO: set up an error view
            String url = getConfig()
                .getLogoutUrl(); //safest thing to do if token is invalid or if there is an error (could be illegal access)
            ServletUtils.issueRedirect(request, response, url, null, true, true);
        }
    }

    protected void verify(HttpServletRequest request, HttpServletResponse response, String sptoken)
        throws ServletException, IOException {

        Client client = ClientResolver.INSTANCE.getClient(request.getServletContext());

        Account account = client.verifyAccountEmail(sptoken);

        /*
        //TODO: session use should be configurable.  USE JWT when not.
        //put the account in the session for easy retrieval later:
        request.getSession().setAttribute("account", account);
        */

        String next = Strings.clean(request.getParameter("next"));

        if (!Strings.hasText(next)) {
            next = getVerifyNextUrl();
        }

        ServletUtils.issueRedirect(request, response, next, null, true, true);
    }
}
