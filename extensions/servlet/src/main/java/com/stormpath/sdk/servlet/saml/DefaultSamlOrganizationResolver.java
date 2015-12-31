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
package com.stormpath.sdk.servlet.saml;

import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.idsite.DefaultIdSiteOrganizationContext;
import com.stormpath.sdk.servlet.idsite.IdSiteOrganizationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC8
 */
public class DefaultSamlOrganizationResolver implements Resolver<SamlOrganizationContext> {

    private Resolver<String> organizationNameKeyResolver;

    private Boolean useSubdomain;

    private Boolean showOrganizationField;

    public void setOrganizationNameKeyResolver(Resolver<String> organizationNameKeyResolver) {
        this.organizationNameKeyResolver = organizationNameKeyResolver;
    }

    public void setUseSubdomain(Boolean useSubdomain) {
        this.useSubdomain = useSubdomain;
    }

    public void setShowOrganizationField(Boolean showOrganizationField) {
        this.showOrganizationField = showOrganizationField;
    }

    @Override
    public SamlOrganizationContext get(HttpServletRequest request, HttpServletResponse response) {

        String subdomain = organizationNameKeyResolver.get(request, response);

        return new DefaultSamlOrganizationContext(subdomain, useSubdomain, showOrganizationField);
    }
}
