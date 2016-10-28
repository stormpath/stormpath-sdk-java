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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.saml.SamlIdpUrlBuilder;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.saml.SamlOrganizationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC8
 */
public class SamlController extends AbstractController {

    private ServerUriResolver serverUriResolver;

    private String callbackUri;

    private String samlUri;

    private String alreadyLoggedInUri = "/";

    //Todo: may want to refactor IdSiteOrganizationResolver to be more polymorphic rather than have a separate SamlOrganizationResolver
    private Resolver<SamlOrganizationContext> samlOrganizationResolver;

    public void setServerUriResolver(ServerUriResolver serverUriResolver) {
        this.serverUriResolver = serverUriResolver;
    }

    public void setCallbackUri(String callbackUri) {
        this.callbackUri = callbackUri;
    }

    public void setAlreadyLoggedInUri(String alreadyLoggedInUri) {
        this.alreadyLoggedInUri = alreadyLoggedInUri;
    }

    public void setSamlUri(String samlUri) {
        this.samlUri = samlUri;
    }

    public void setSamlOrganizationResolver(Resolver<SamlOrganizationContext> samlOrganizationResolver) {
        this.samlOrganizationResolver = samlOrganizationResolver;
    }

    public void init() {
        Assert.notNull(serverUriResolver, "Application must be configured.");
        Assert.notNull(callbackUri, "callbackUri must be configured.");
        Assert.notNull(samlOrganizationResolver, "idSiteOrganizationResolver must be configured.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    protected Application getApplication(HttpServletRequest request) {
        return ApplicationResolver.INSTANCE.getApplication(request);
    }

    protected String buildCallbackUri(HttpServletRequest request) {

        String uri = this.serverUriResolver.getServerUri(request);

        String contextPath = request.getContextPath();
        if (contextPath == null || contextPath.equals("/")) {
            contextPath = "";
        }
        if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }

        uri += contextPath + this.callbackUri;

        return uri;
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //Let's redirect to "alreadyLoggedInUri" if the user is already logged in
        if (AccountResolver.INSTANCE.getAccount(request) != null) {
            return new DefaultViewModel(alreadyLoggedInUri).setRedirect(true);
        }

        String samlUrl = createSamlUrl(request);

        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "-1");

        return new DefaultViewModel(samlUrl).setRedirect(true);
    }

    protected String createSamlUrl(HttpServletRequest request) {
        SamlIdpUrlBuilder builder = createSamlIdpUrlBuilder(request);
        return builder.build();
    }

    protected SamlIdpUrlBuilder createSamlIdpUrlBuilder(HttpServletRequest request) {

        Application application = getApplication(request);

        String callbackUri = buildCallbackUri(request);

        SamlIdpUrlBuilder builder = application.newSamlIdpUrlBuilder().setCallbackUri(callbackUri);

        String ash = request.getParameter("href");
        // if href is passed in as a parameter, set it as a claim in the JWT
        if (ash != null) {
            builder.setAccountStoreHref(ash);
        }

        SamlOrganizationContext orgCtx = samlOrganizationResolver.get(request, null);

        if (orgCtx != null) {

            String nameKey = orgCtx.getOrganizationNameKey();
            if (Strings.hasText(nameKey)) {

                builder.setOrganizationNameKey(nameKey);
            }
        }

        if (this.samlUri != null) {
            builder.setPath(this.samlUri);
        }
        return builder;
    }
}
