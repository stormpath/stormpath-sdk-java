/*
 * Copyright 2016 Stormpath, Inc.
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.filter.LoginPageRedirector;
import com.stormpath.sdk.servlet.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.0
 */
public class MeController extends AbstractController {

    private ExpandsResolver expandsResolver;
    private AccountModelFactory accountModelFactory;
    private ObjectMapper objectMapper;
    private LoginPageRedirector loginPageRedirector;

    public MeController() {
        this.accountModelFactory = new DefaultAccountModelFactory();
    }

    @Override
    public void init() throws Exception {
        Assert.hasText(this.uri, "uri cannot be null or empty.");
        Assert.notNull(this.accountModelFactory, "accountModelFactory cannot be null.");
        Assert.notNull(this.objectMapper, "objectMapper cannot be null.");
        Assert.notEmpty(this.produces, "produces cannot be null or empty");
        Assert.notNull(this.applicationResolver, "applicationResolver cannot be null.");
    }

    public LoginPageRedirector getLoginPageRedirector() {
        return loginPageRedirector;
    }

    public void setLoginPageRedirector(LoginPageRedirector loginPageRedirector) {
        this.loginPageRedirector = loginPageRedirector;
    }

    /**
     * @since 1.2.0
     */
    public void setExpandsResolver(ExpandsResolver expandsResolver){
        this.expandsResolver = expandsResolver;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return false;
    }

    /**
     * Successful JSON login will forward here as a POST so that the account model is returned.
     * <p>
     * See: https://github.com/stormpath/stormpath-sdk-java/issues/682
     *
     * @since 1.0.0
     */
    @Override
    protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return doGet(request, response);
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Account account = AccountResolver.INSTANCE.getAccount(request);

        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        //Since we don't have a restrict authentication mechanism for spring-webmvc we check if the account is there and redirect to login as per spec
        if (account == null) {
            if (isHtmlPreferred(request, response)) {
                loginPageRedirector.redirectToLoginPage(request, response);
            }
            if (isJsonPreferred(request, response)) {
                Application application = applicationResolver.getApplication(request.getServletContext());
                String bearerRealm = String.format("Bearer realm=\"%s\"", application.getName());
                response.addHeader("WWW-Authenticate", bearerRealm);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return null;
        }

        //There is an issue with spring boot webmvc, it register ContentNegotiatingViewResolver with the highest priority,
        //and this view resolver doesn't allow to override the view to force JSON even if the Accept header has a different content type
        //for example if the user goes to the /me in a browser it would try to render a thymeleaf view, so instead of returning a view
        //we write directly to the response since no matter what we always return JSON for this controller.
        //This way we don't introduce any custom view resolver that might have issues with the user application.
        objectMapper.writeValue(response.getOutputStream(), java.util.Collections.singletonMap("account", accountModelFactory.toMap(account, expandsResolver.getExpands())));
        return null;
    }
}
