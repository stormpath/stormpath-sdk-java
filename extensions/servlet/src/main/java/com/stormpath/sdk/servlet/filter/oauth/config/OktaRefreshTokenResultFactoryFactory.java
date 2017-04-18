package com.stormpath.sdk.servlet.filter.oauth.config;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.oauth.OktaRefreshTokenResultFactory;
import com.stormpath.sdk.servlet.filter.oauth.RefreshTokenResultFactory;

import javax.servlet.ServletContext;

/**
 *
 */
public class OktaRefreshTokenResultFactoryFactory extends ConfigSingletonFactory<RefreshTokenResultFactory> {

    @Override
    protected RefreshTokenResultFactory createInstance(ServletContext servletContext) throws Exception {
        Application application = (Application)servletContext.getAttribute(Application.class.getName());
        return new OktaRefreshTokenResultFactory(application);
    }
}