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
import com.stormpath.sdk.servlet.mvc.SamlController;
import com.stormpath.sdk.servlet.organization.DefaultOrganizationNameKeyResolver;
import com.stormpath.sdk.servlet.saml.DefaultSamlOrganizationResolver;
import com.stormpath.sdk.servlet.util.SubdomainResolver;

/**
 * @since 1.0.0
 */
public class SamlFilterFactory extends ControllerFilterFactory<SamlController> {

    @Override
    protected SamlController newController() {
        return new SamlController();
    }

    @Override
    protected void configure(SamlController controller, Config config) throws Exception {

        SubdomainResolver subdomainResolver = new SubdomainResolver();
        subdomainResolver.setBaseDomainName(config.get("stormpath.web.application.domain"));

        DefaultOrganizationNameKeyResolver organizationNameKeyResolver = new DefaultOrganizationNameKeyResolver();
        organizationNameKeyResolver.setSubdomainResolver(subdomainResolver);

        DefaultSamlOrganizationResolver samlOrganizationResolver = new DefaultSamlOrganizationResolver();
        samlOrganizationResolver.setOrganizationNameKeyResolver(organizationNameKeyResolver);

        controller.setServerUriResolver(new DefaultServerUriResolver());
        controller.setCallbackUri(getConfig().get("stormpath.web.callback.uri"));
        controller.setAlreadyLoggedInUri(getConfig().getLoginConfig().getNextUri());
        controller.setSamlOrganizationResolver(samlOrganizationResolver);
    }
}
