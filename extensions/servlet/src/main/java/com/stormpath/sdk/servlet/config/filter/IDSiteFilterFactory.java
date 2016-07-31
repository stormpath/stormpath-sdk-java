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
package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.DefaultServerUriResolver;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.idsite.DefaultIdSiteOrganizationResolver;
import com.stormpath.sdk.servlet.mvc.IdSiteController;
import com.stormpath.sdk.servlet.organization.DefaultOrganizationNameKeyResolver;
import com.stormpath.sdk.servlet.util.SubdomainResolver;

/**
 * @since 1.0.0
 */
public abstract class IDSiteFilterFactory extends ControllerFilterFactory<IdSiteController> {

    @Override
    protected IdSiteController newController() {
        return new IdSiteController();
    }

    @Override
    protected void configure(IdSiteController c, Config config) throws Exception {

        SubdomainResolver subdomainResolver = new SubdomainResolver();
        subdomainResolver.setBaseDomainName(config.get("stormpath.web.application.domain"));

        DefaultOrganizationNameKeyResolver organizationNameKeyResolver = new DefaultOrganizationNameKeyResolver();
        organizationNameKeyResolver.setSubdomainResolver(subdomainResolver);

        DefaultIdSiteOrganizationResolver idSiteOrganizationResolver = new DefaultIdSiteOrganizationResolver();
        idSiteOrganizationResolver.setOrganizationNameKeyResolver(organizationNameKeyResolver);

        c.setServerUriResolver(new DefaultServerUriResolver());
        c.setCallbackUri(getConfig().getCallbackUri());
        c.setAlreadyLoggedInUri(config.get("stormpath.web.login.nextUri"));
        c.setIdSiteOrganizationResolver(idSiteOrganizationResolver);

        ControllerFilter filter = new ControllerFilter();
        filter.setProducedMediaTypes(config.getProducedMediaTypes());
        filter.setController(c);

        //Let's give the chance to sub-classes of this factory to configure this controller as well
        doConfigure(c, config);
    }

    public abstract void doConfigure(IdSiteController c, Config config);
}
