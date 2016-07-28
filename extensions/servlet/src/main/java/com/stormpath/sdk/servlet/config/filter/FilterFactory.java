package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;

import javax.servlet.Filter;
import javax.servlet.ServletContext;

/**
 * @param <T>
 * @since 1.0.0
 */
public abstract class FilterFactory<T extends Filter> extends ConfigSingletonFactory<T> {

    @Override
    protected T createInstance(ServletContext servletContext) throws Exception {
        return createInstance(servletContext, getConfig());
    }

    protected abstract T createInstance(ServletContext servletContext, Config config) throws Exception;
}
