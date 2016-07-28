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
    protected void configure(SamlController c, Config config) throws Exception {

        SubdomainResolver subdomainResolver = new SubdomainResolver();
        subdomainResolver.setBaseDomainName(config.get("stormpath.web.application.domain"));

        DefaultOrganizationNameKeyResolver organizationNameKeyResolver = new DefaultOrganizationNameKeyResolver();
        organizationNameKeyResolver.setSubdomainResolver(subdomainResolver);

        DefaultSamlOrganizationResolver samlOrganizationResolver = new DefaultSamlOrganizationResolver();
        samlOrganizationResolver.setOrganizationNameKeyResolver(organizationNameKeyResolver);

        c.setServerUriResolver(new DefaultServerUriResolver());
        c.setCallbackUri(getConfig().get("stormpath.web.callback.uri"));
        c.setAlreadyLoggedInUri(getConfig().getLoginConfig().getNextUri());
        c.setSamlOrganizationResolver(samlOrganizationResolver);
    }
}
