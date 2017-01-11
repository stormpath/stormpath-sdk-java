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
package com.stormpath.spring.config;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.authc.LogoutRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultLogoutRequestEvent;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import com.stormpath.sdk.servlet.mvc.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @since 1.0.RC5
 */
public class StormpathLogoutHandler implements LogoutHandler {

    private static Logger log = LoggerFactory.getLogger(StormpathLogoutHandler.class);

    private Saver<AuthenticationResult> authenticationResultSaver;

    @Autowired
    private Publisher<RequestEvent> stormpathRequestEventPublisher;

    @Value("#{ @environment['stormpath.web.idSite.enabled'] ?: false }")
    protected boolean idSiteEnabled;

    @Value("#{ @environment['stormpath.web.logout.nextUri'] ?: '/' }")
    protected String logoutNextUri;

    @Autowired
    @Qualifier("stormpathLogoutController")
    protected Controller logoutController;

    @Autowired
    protected Client client;

    @Autowired
    protected AccountResolver accountResolver;

    @Autowired
    protected List<MediaType> stormpathProducedMediaTypes;

    public StormpathLogoutHandler(Saver<AuthenticationResult> authenticationResultSaver) {
        this.authenticationResultSaver = authenticationResultSaver;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Account account = null;

        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                String accountHref = ((User) principal).getUsername();
                if (accountHref != null && accountHref.contains("/accounts/")) {
                    account = client.getResource(accountHref, Account.class);
                }
            }
        }

        authenticationResultSaver.set(request, response, null);

        if (idSiteEnabled) {
            try {
                logoutController.handleRequest(request, response);
            } catch (Exception e) {
                log.error("Couldn't invoke logout controller");
                throw new RuntimeException(e);
            }
        }

        //This logout handler is invoked twice when IDSite is enabled. In the second time the account is not in the request any longer, it has all been cleared in the first round
        if (account != null || !idSiteEnabled) {
            //This code will be executed when:
            //1. the account is not null, or
            //2. IdSite is disabled
            LogoutRequestEvent e = createLogoutEvent(request, response, account);
            stormpathRequestEventPublisher.publish(e);
        }

        // https://github.com/stormpath/stormpath-sdk-java/issues/915
        // and to ensure that the request is forwarded and not redirected
        // for JSON logouts per for stormpath-framework-spec and enforced
        // by the stormpath-framework-tck
        try {
            HttpServletRequestWrapper r = new HttpServletRequestWrapper(request) {
                @Override
                public Cookie[] getCookies() { return null; }
            };
            MediaType requestMediaType =
                ContentNegotiationResolver.INSTANCE.getContentType(request, response, stormpathProducedMediaTypes);
            if (MediaType.APPLICATION_JSON.equals(requestMediaType)) {
                request.getRequestDispatcher(logoutNextUri).forward(r, response);
            }
        } catch (ServletException | IOException | UnresolvedMediaTypeException e) {
            log.error("Unable to forward logout request to: {}. Error: {}.", logoutNextUri, e.getMessage(), e);
        }
    }

    protected LogoutRequestEvent createLogoutEvent(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   Account account) {
        return new DefaultLogoutRequestEvent(request, response, account);
    }
}
