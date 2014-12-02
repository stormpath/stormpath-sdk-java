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
import com.stormpath.sdk.servlet.account.event.VerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.impl.DefaultVerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
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

    public static final String EVENT_PUBLISHER = "stormpath.web.request.event.publisher";

    private Publisher<RequestEvent> eventPublisher;

    @Override
    protected void onInit() throws ServletException {
        this.eventPublisher = getConfig().getInstance(EVENT_PUBLISHER);
    }

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

    public Publisher<RequestEvent> getEventPublisher() {
        return this.eventPublisher;
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

        Client client = getClient();

        Account account = client.verifyAccountEmail(sptoken);

        RequestEvent e = createVerifiedEvent(request, response, account);
        publish(e);

        String next = Strings.clean(request.getParameter("next"));

        if (!Strings.hasText(next)) {
            next = getVerifyNextUrl();
        }

        ServletUtils.issueRedirect(request, response, next, null, true, true);
    }

    protected VerifiedAccountRequestEvent createVerifiedEvent(HttpServletRequest request, HttpServletResponse response,
                                                              Account account) {
        return new DefaultVerifiedAccountRequestEvent(request, response, account);
    }

    protected void publish(RequestEvent e) throws ServletException {
        try {
            getEventPublisher().publish(e);
        } catch (Exception ex) {
            String msg = "Unable to publish verified account request event: " + ex.getMessage();
            throw new ServletException(msg, ex);
        }
    }
}
