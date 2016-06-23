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
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultSuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import com.stormpath.sdk.servlet.mvc.WebHandler;
import com.stormpath.spring.security.provider.StormpathUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @since 1.0.RC5
 */
public class StormpathLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Value("#{ @environment['stormpath.web.me.uri'] ?: '/me' }")
    protected String meUri;

    private static final Logger log = LoggerFactory.getLogger(StormpathLoginSuccessHandler.class);

    private Client stormpathClient;

    private Saver<AuthenticationResult> authenticationResultSaver;

    private List<MediaType> supportedMediaTypes;

    @Autowired(required = false)
    @Qualifier("loginPostHandler")
    protected WebHandler loginPostHandler;

    @Autowired
    private Publisher<RequestEvent> stormpathRequestEventPublisher;

    public StormpathLoginSuccessHandler(Client client, Saver<AuthenticationResult> saver, String produces) {
        this.stormpathClient = client;
        this.authenticationResultSaver = saver;
        this.supportedMediaTypes = MediaType.parseMediaTypes(produces);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        saveAccount(request, response, authentication);

        SuccessfulAuthenticationRequestEvent e = createSuccessEvent(request, response, getAccount(authentication));
        stormpathRequestEventPublisher.publish(e);

        // Content Negotiation per https://github.com/stormpath/stormpath-sdk-java/issues/682
        try {
            boolean shouldContinue = true;

            if (loginPostHandler != null) {
                shouldContinue = loginPostHandler.handle(request, response, getAccount(authentication));
            }
            if (shouldContinue) {
                MediaType mediaType =
                        ContentNegotiationResolver.INSTANCE.getContentType(request, response, supportedMediaTypes);
                if (MediaType.APPLICATION_JSON.equals(mediaType)) {
                    request.getRequestDispatcher(meUri).forward(request, response);
                } else {
                    super.onAuthenticationSuccess(request, response, authentication);
                }
            }
        } catch (UnresolvedMediaTypeException ex) {
            log.error("Couldn't resolve media type: {}", ex.getMessage(), ex);
        }
    }

    protected void saveAccount(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        Account account = getAccount(authentication);
        AuthenticationResult result = new TransientAuthenticationResult(account);
        authenticationResultSaver.set(request, response, result);
    }

    protected Account getAccount(Authentication authentication) {
        String accountHref = ((StormpathUserDetails) authentication.getPrincipal()).getProperties().get("href");
        return stormpathClient.getResource(accountHref, Account.class);
    }

    protected SuccessfulAuthenticationRequestEvent createSuccessEvent(HttpServletRequest request,
                                                                      HttpServletResponse response,
                                                                      Account account) {
        return new DefaultSuccessfulAuthenticationRequestEvent(request, response, null, new TransientAuthenticationResult(account));
    }

}
