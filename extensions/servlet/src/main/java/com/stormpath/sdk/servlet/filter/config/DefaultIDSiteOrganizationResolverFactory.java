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
package com.stormpath.sdk.servlet.filter.config;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.idsite.DefaultIdSiteOrganizationResolver;
import com.stormpath.sdk.servlet.idsite.IdSiteOrganizationContext;
import com.stormpath.sdk.servlet.organization.DefaultOrganizationNameKeyResolver;
import com.stormpath.sdk.servlet.util.SubdomainResolver;

import javax.servlet.ServletContext;

public class DefaultIDSiteOrganizationResolverFactory extends ConfigSingletonFactory<Resolver<IdSiteOrganizationContext>> {

    protected static final String USE_SUBDOMAIN = "stormpath.web.idSite.useSubdomain";
    protected static final String SHOW_ORGANIZATION_FIELD = "stormpath.web.idSite.showOrganizationField";

    protected Resolver<IdSiteOrganizationContext> createInstance(ServletContext servletContext) throws Exception {
        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);
        DefaultIdSiteOrganizationResolver resolver = new DefaultIdSiteOrganizationResolver();
        DefaultOrganizationNameKeyResolver organizationNameKeyResolver = new DefaultOrganizationNameKeyResolver();
        SubdomainResolver subdomainResolver = new SubdomainResolver();
        subdomainResolver.setBaseDomainName(getConfig().getWebApplicationDomain());
        organizationNameKeyResolver.setSubdomainResolver(subdomainResolver);
        resolver.setOrganizationNameKeyResolver(organizationNameKeyResolver);
        String useSubdomain = config.get(USE_SUBDOMAIN);
        if (Strings.hasText(useSubdomain)) {
            resolver.setUseSubdomain(Boolean.getBoolean(useSubdomain));
        } else {
            resolver.setUseSubdomain(false);
        }
        String showOrganizationField = config.get(SHOW_ORGANIZATION_FIELD);
        if (Strings.hasText(useSubdomain)) {
            resolver.setShowOrganizationField(Boolean.getBoolean(showOrganizationField));
        } else {
            resolver.setShowOrganizationField(false);
        }
        return resolver;
    }
}
