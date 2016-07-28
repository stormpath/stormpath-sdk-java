package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.AccessControlFilter;
import com.stormpath.sdk.servlet.filter.UnauthenticatedHandler;
import com.stormpath.sdk.servlet.filter.UnauthorizedHandler;

import javax.servlet.ServletContext;

public abstract class AccessControlFilterFactory<T extends AccessControlFilter> extends FilterFactory<T> {

    protected static final String UNAUTHENTICATED_HANDLER = "stormpath.web.authc.unauthenticatedHandler";
    protected static final String UNAUTHORIZED_HANDLER = "stormpath.web.authz.unauthorizedHandler";

    @Override
    protected T createInstance(ServletContext servletContext, Config config) throws Exception {
        T f = newInstance();

        UnauthenticatedHandler authc = config.getInstance(UNAUTHENTICATED_HANDLER);
        f.setUnauthenticatedHandler(authc);

        UnauthorizedHandler authz = config.getInstance(UNAUTHORIZED_HANDLER);
        f.setUnauthorizedHandler(authz);

        configure(f, config);

        return f;
    }

    protected abstract void configure(T f, Config config);

    protected abstract T newInstance();
}
