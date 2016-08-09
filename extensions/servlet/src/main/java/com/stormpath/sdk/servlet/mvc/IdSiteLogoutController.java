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

import com.stormpath.sdk.idsite.IdSiteUrlBuilder;
import com.stormpath.sdk.idsite.LogoutResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.idsite.IdSiteOrganizationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IdSiteLogoutController extends LogoutController {

    private ServerUriResolver serverUriResolver;
    private String idSiteResultUri;
    private Resolver<IdSiteOrganizationContext> idSiteOrganizationResolver;
    private Controller idSiteController; //not injected - created during init()

    public void setServerUriResolver(ServerUriResolver serverUriResolver) {
        this.serverUriResolver = serverUriResolver;
    }

    public void setIdSiteResultUri(String idSiteResultUri) {
        this.idSiteResultUri = idSiteResultUri;
    }

    public void setIdSiteOrganizationResolver(Resolver<IdSiteOrganizationContext> idSiteOrganizationResolver) {
        this.idSiteOrganizationResolver = idSiteOrganizationResolver;
    }

    public void init() {
        super.init();
        Assert.notNull(serverUriResolver, "serverUriResolver must be configured.");
        Assert.hasText(idSiteResultUri, "idSiteResultUri must be configured.");
        Assert.notNull(idSiteOrganizationResolver, "idSiteOrganizationController must be configured.");

        IdSiteController controller = new LogoutIdSiteController();
        controller.setAlreadyLoggedInUri(nextUri); //this will not really have any affect on logout but we need to set it otherwise controller#init() will throw
        controller.setServerUriResolver(serverUriResolver);
        controller.setCallbackUri(idSiteResultUri);
        controller.setIdSiteOrganizationResolver(idSiteOrganizationResolver);
        controller.init();

        this.idSiteController = controller;
    }

    @Override
    public ViewModel handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (request.getAttribute(LogoutResult.class.getName()) != null) {
            //We're currently processing a reply from ID site, don't send back to ID site:
            return new DefaultViewModel(nextUri).setRedirect(true);
        } else {
            //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/882
            //Ensure the local application user state is cleared but only once
            super.handleRequest(request, response);
        }

        //redirect to ID Site to perform the SSO logout to effectively log out the user across all applications:
        return idSiteController.handleRequest(request, response);
    }

    private static class LogoutIdSiteController extends IdSiteController {

        @Override
        protected IdSiteUrlBuilder createIdSiteUrlBuilder(HttpServletRequest request) {
            return super.createIdSiteUrlBuilder(request).forLogout();
        }

        /**
         * @since 1.0.0
         */
        @Override
        protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

            String idSiteUrl = createIdSiteUrl(request);

            response.setHeader("Cache-control", "no-cache, no-store");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "-1");

            return new DefaultViewModel(idSiteUrl).setRedirect(true);
        }
    }
}
