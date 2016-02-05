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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.event.VerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.impl.DefaultVerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @since 1.0.RC4
 */
public class VerifyController extends AbstractController {

    private String nextUri;
    private String logoutUri;
    private String sendVerificationEmailUri;
    private Client client;
    private Publisher<RequestEvent> eventPublisher;

    public void init() {
        Assert.hasText(nextUri, "nextUri cannot be null or empty.");
        Assert.hasText(logoutUri, "logoutUri cannot be null or empty.");
        Assert.hasText(sendVerificationEmailUri, "sendVerificationEmailUri cannot be null or empty.");
        Assert.notNull(client, "client cannot be null.");
        Assert.notNull(eventPublisher, "eventPublisher cannot be null.");
    }

    public String getNextUri() {
        return nextUri;
    }

    public void setNextUri(String nextUri) {
        this.nextUri = nextUri;
    }

    public String getLogoutUri() {
        return logoutUri;
    }

    public void setLogoutUri(String logoutUri) {
        this.logoutUri = logoutUri;
    }

    /* @since 1.0.RC8.3 */
    public String getSendVerificationEmailUri() {
        return sendVerificationEmailUri;
    }

    /* @since 1.0.RC8.3 */
    public void setSendVerificationEmailUri(String sendVerificationEmailUri) {
        this.sendVerificationEmailUri = sendVerificationEmailUri;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Publisher<RequestEvent> getEventPublisher() {
        return this.eventPublisher;
    }

    public void setEventPublisher(Publisher<RequestEvent> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        String sptoken = Strings.clean(request.getParameter("sptoken"));

        if (sptoken == null) {
            //redirect to send verification email form
            String sendVerificationEmailUri = getSendVerificationEmailUri();
            return new DefaultViewModel(sendVerificationEmailUri).setRedirect(true);
        }

        try {
            return verify(request, response, sptoken);
        } catch (Exception e) {
            //safest thing to do if token is invalid or if there is an error (could be illegal access)
            String logoutUri = getLogoutUri();
            return new DefaultViewModel(logoutUri).setRedirect(true);
        }
    }

    protected ViewModel verify(HttpServletRequest request, HttpServletResponse response, String sptoken)
        throws ServletException, IOException {

        Client client = getClient();

        Account account = client.verifyAccountEmail(sptoken);

        RequestEvent e = createVerifiedEvent(request, response, account);
        publish(e);

        String next = Strings.clean(request.getParameter("next"));

        if (!Strings.hasText(next)) {
            next = getNextUri();
        }

        return new DefaultViewModel(next).setRedirect(true);
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
