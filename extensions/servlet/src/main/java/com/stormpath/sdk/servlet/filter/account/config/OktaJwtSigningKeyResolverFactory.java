package com.stormpath.sdk.servlet.filter.account.config;

import com.stormpath.sdk.impl.okta.OktaSigningKeyResolver;
import com.stormpath.sdk.servlet.application.okta.OktaJwtSigningKeyResolver;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.account.DefaultJwtSigningKeyResolver;
import com.stormpath.sdk.servlet.filter.account.JwtSigningKeyResolver;

import javax.servlet.ServletContext;

/**
 */
public class OktaJwtSigningKeyResolverFactory extends ConfigSingletonFactory<JwtSigningKeyResolver> {

    @Override
    protected JwtSigningKeyResolver createInstance(ServletContext servletContext) throws Exception {

        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);
        OktaSigningKeyResolver resolver = config.getInstance("stormpath.jwt.signingKey.resolver");

        return new OktaJwtSigningKeyResolver(resolver);
    }
}
