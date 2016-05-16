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
import com.stormpath.sdk.servlet.account.event.impl.DefaultVerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.filter.ControllerConfigResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @since 1.0.RC4
 */
public class VerifyController extends AbstractController {

    private String logoutUri;
    private String sendVerificationEmailUri;
    private Client client;

    public VerifyController() {
        super();
    }

    public VerifyController(ControllerConfigResolver controllerConfigResolver,
                            String logoutUri,
                            String sendVerificationEmailUri,
                            Client client) {
        super(controllerConfigResolver);

        this.logoutUri = logoutUri;
        this.client = client;
        this.sendVerificationEmailUri = sendVerificationEmailUri;

        Assert.hasText(logoutUri, "logoutUri cannot be null or empty.");
        Assert.hasText(sendVerificationEmailUri, "sendVerificationEmailUri cannot be null or empty.");
        Assert.notNull(client, "client cannot be null.");
        Assert.notNull(eventPublisher, "eventPublisher cannot be null.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String sptoken = Strings.clean(request.getParameter("sptoken"));

        if (sptoken == null) {
            //redirect to send verification email form
            return new DefaultViewModel(sendVerificationEmailUri).setRedirect(true);
        }

        try {
            return verify(request, response, sptoken);
        } catch (Exception e) {
            //safest thing to do if token is invalid or if there is an error (could be illegal access)
            return new DefaultViewModel(logoutUri).setRedirect(true);
        }
    }

    protected ViewModel verify(HttpServletRequest request, HttpServletResponse response, String sptoken)
            throws ServletException, IOException {

        Account account = client.verifyAccountEmail(sptoken);

        publishRequestEvent(new DefaultVerifiedAccountRequestEvent(request, response, account));

        return new DefaultViewModel(nextUri).setRedirect(true);
    }
}
