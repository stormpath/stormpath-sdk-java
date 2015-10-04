package com.stormpath.sdk.servlet.idsite;

import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC5
 */
public class DefaultIdSiteOrganizationResolver implements Resolver<IdSiteOrganizationContext> {

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
    public IdSiteOrganizationContext get(HttpServletRequest request, HttpServletResponse response) {

        String subdomain = organizationNameKeyResolver.get(request, response);

        return new DefaultIdSiteOrganizationContext(subdomain, useSubdomain, showOrganizationField);
    }
}
