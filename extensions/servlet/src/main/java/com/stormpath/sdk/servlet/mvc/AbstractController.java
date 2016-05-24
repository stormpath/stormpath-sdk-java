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

import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.ControllerConfigResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.UserAgents;
import com.stormpath.sdk.servlet.i18n.MessageSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public abstract class AbstractController implements Controller {

    private static final HttpServlet DEFAULT_HANDLER = new HttpServlet() {
    };

    protected String nextUri;
    protected String view;
    protected String uri;
    protected MessageSource messageSource;
    protected Publisher<RequestEvent> eventPublisher;

    protected String controllerKey;
    private Resolver<Locale> localeResolver;

    public AbstractController(ControllerConfigResolver controllerConfigResolver) {
        this.nextUri = controllerConfigResolver.getNextUri();
        this.messageSource = controllerConfigResolver.getMessageSource();
        this.localeResolver = controllerConfigResolver.getLocaleResolver();
        this.view = controllerConfigResolver.getView();
        this.uri = controllerConfigResolver.getUri();
        this.controllerKey = controllerConfigResolver.getControllerKey();
        this.eventPublisher = controllerConfigResolver.getRequestEventPublisher();

        Assert.hasText(this.nextUri, "nextUri property cannot be null or empty.");
        Assert.hasText(this.view, "view cannot be null or empty.");
        Assert.hasText(this.uri, "uri cannot be null or empty.");
        Assert.notNull(this.messageSource, "messageSource cannot be null.");
        Assert.notNull(this.localeResolver, "localeResolver cannot be null.");
        Assert.hasText(this.controllerKey, "controllerKey cannot be null.");
    }

    protected AbstractController() {
    }

    protected Map<String, Object> newModel() {
        return new HashMap<String, Object>();
    }

    /**
     * Method returns true if the controller doesn't allow requests if there is a user already authenticated,
     * this is true for most controllers.
     *
     * @return True if controller doesn't allow request when user is authenticated, false otherwise
     */
    public abstract boolean isNotAllowedIfAuthenticated();

    protected String i18n(HttpServletRequest request, String key) {
        Locale locale = localeResolver.get(request, null);
        return messageSource.getMessage(key, locale);
    }

    protected String i18n(HttpServletRequest request, String key, Object... args) {
        Locale locale = localeResolver.get(request, null);
        return messageSource.getMessage(key, locale, args);
    }

    protected boolean isJsonPreferred(HttpServletRequest request) {
        return UserAgents.get(request).isJsonPreferred();
    }

    @Override
    public ViewModel handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String method = request.getMethod();

        boolean hasAccount = AccountResolver.INSTANCE.hasAccount(request);

        if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            if (isNotAllowedIfAuthenticated() && hasAccount) {
                return new DefaultViewModel(nextUri).setRedirect(true);
            }
            return doGet(request, response);
        } else if (HttpMethod.POST.name().equalsIgnoreCase(method)) {
            if (isNotAllowedIfAuthenticated() && hasAccount) {
                response.sendError(403);
                return null;
            }
            return doPost(request, response);
        } else {
            return service(request, response);
        }
    }

    protected ViewModel service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        DEFAULT_HANDLER.service(request, response);
        return null;
    }

    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return service(request, response);
    }

    protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return service(request, response);
    }

    protected void publishRequestEvent(RequestEvent e) throws ServletException {
        if (e != null) {
            try {
                eventPublisher.publish(e);
            } catch (Exception ex) {
                String msg = "Unable to publish registered account request event: " + ex.getMessage();
                throw new ServletException(msg, ex);
            }
        }
    }
}
