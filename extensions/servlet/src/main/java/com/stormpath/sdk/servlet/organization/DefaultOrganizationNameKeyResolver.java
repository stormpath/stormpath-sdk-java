package com.stormpath.sdk.servlet.organization;

import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @since 1.0.RC5
 */
public class DefaultOrganizationNameKeyResolver implements Resolver<String> {

    private Resolver<List<String>> subdomainResolver;

    public void setSubdomainResolver(Resolver<List<String>> subdomainResolver) {
        this.subdomainResolver = subdomainResolver;
    }

    @Override
    public String get(HttpServletRequest request, HttpServletResponse response) {

        List<String> subdomains = subdomainResolver.get(request, null);

        String subdomain = null;

        if (!Collections.isEmpty(subdomains)) {
            subdomain = subdomains.get(0);
        }

        return subdomain;
    }
}
