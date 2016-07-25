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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.idsite.DefaultIdSiteOrganizationResolver;
import com.stormpath.sdk.servlet.mvc.IdSiteController;
import com.stormpath.sdk.servlet.organization.DefaultOrganizationNameKeyResolver;
import com.stormpath.sdk.servlet.util.SubdomainResolver;

import javax.servlet.ServletException;

/**
 * @since 1.0.0
 */
public class IDSiteFilter extends ControllerFilter {

    @Override
    protected void onInit() throws ServletException {
        SubdomainResolver subdomainResolver = new SubdomainResolver();
        subdomainResolver.setBaseDomainName(getConfig().getWebApplicationDomain());

        DefaultOrganizationNameKeyResolver organizationNameKeyResolver = new DefaultOrganizationNameKeyResolver();
        organizationNameKeyResolver.setSubdomainResolver(subdomainResolver);

        DefaultIdSiteOrganizationResolver idSiteOrganizationResolver = new DefaultIdSiteOrganizationResolver();
        idSiteOrganizationResolver.setOrganizationNameKeyResolver(organizationNameKeyResolver);

        IdSiteController controller = new IdSiteController();
        controller.setServerUriResolver(new DefaultServerUriResolver());
        controller.setCallbackUri(getConfig().getIDSiteResultUri());
        controller.setAlreadyLoggedInUri(getConfig().getLoginControllerConfig().getNextUri());
        controller.setIdSiteOrganizationResolver(idSiteOrganizationResolver);
        controller.setIdSiteUri(getIdSiteUri());
        controller.init();

        setController(controller);
        super.onInit();
    }

    protected String getIdSiteUri() {
        return getConfig().getIDSiteLoginUri();
    }
}
