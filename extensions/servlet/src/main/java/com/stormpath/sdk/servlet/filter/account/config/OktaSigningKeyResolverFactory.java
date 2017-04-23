package com.stormpath.sdk.servlet.filter.account.config;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.okta.DefaultOktaSigningKeyResolver;
import com.stormpath.sdk.impl.okta.OktaSigningKeyResolver;
import com.stormpath.sdk.okta.OIDCWellKnownResource;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;

import javax.servlet.ServletContext;

/**
 */
public class OktaSigningKeyResolverFactory extends ConfigSingletonFactory<OktaSigningKeyResolver> {

    @Override
    protected OktaSigningKeyResolver createInstance(ServletContext servletContext) throws Exception {

        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);
        Client client = ClientResolver.INSTANCE.getClient(servletContext);

        return new DefaultOktaSigningKeyResolver(
                client,
                config.getOktaAuthorizationServerId(),
                config.isAllowApiSecret());
    }
}