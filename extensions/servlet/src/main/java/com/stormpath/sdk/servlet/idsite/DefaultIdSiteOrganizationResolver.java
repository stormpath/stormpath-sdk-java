package com.stormpath.sdk.servlet.idsite;

import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @since 1.0.RC5
 */
public class DefaultIdSiteOrganizationResolver implements Resolver<IdSiteOrganizationContext> {

    private Resolver<List<String>> subdomainResolver;

    private Boolean useSubdomain;

    private Boolean showOrganizationField;

    public void setSubdomainResolver(Resolver<List<String>> subdomainResolver) {
        this.subdomainResolver = subdomainResolver;
    }

    public void setUseSubdomain(Boolean useSubdomain) {
        this.useSubdomain = useSubdomain;
    }

    public void setShowOrganizationField(Boolean showOrganizationField) {
        this.showOrganizationField = showOrganizationField;
    }

    @Override
    public IdSiteOrganizationContext get(HttpServletRequest request, HttpServletResponse response) {

        List<String> subdomains = subdomainResolver.get(request, null);

        String subdomain = null;

        if (subdomains.size() == 1) {
            subdomain = subdomains.get(0);
        }

        return new DefaultIdSiteOrganizationContext(subdomain, useSubdomain, showOrganizationField);
    }
}
