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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SamlLogoutController extends LogoutController {

    private ServerUriResolver serverUriResolver;
    private String samlResultUri;

    // not implemented yet
    //private Resolver<SamlOrganizationContext> samlOrganizationResolver;

    public void setServerUriResolver(ServerUriResolver serverUriResolver) {
        this.serverUriResolver = serverUriResolver;
    }

    public void setSamlResultUri(String samlResultUri) {
        this.samlResultUri = samlResultUri;
    }

    // not implemented yet
    /*
    public void setSamlOrganizationResolver(Resolver<SamlOrganizationContext> SamlOrganizationResolver) {
        this.samlOrganizationResolver = samlOrganizationResolver;
    }
    */

    public void init() {
        Assert.notNull(serverUriResolver, "serverUriResolver must be configured.");

        //not implemented yet
        //controller.setSamlOrganizationResolver(idSiteOrganizationResolver);
    }

    @Override
    public boolean isNotAllowIfAuthenticated() {
        return false;
    }

    @Override
    public ViewModel handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //ensure the local application user state is cleared no matter what:
        ViewModel vm = super.handleRequest(request, response);

        //not dealing with SAML SSO logout right now.
        return vm;
    }
}
