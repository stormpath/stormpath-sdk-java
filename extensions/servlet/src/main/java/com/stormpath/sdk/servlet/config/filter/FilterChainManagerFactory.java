package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.DefaultFilter;
import com.stormpath.sdk.servlet.filter.DefaultFilterChainManager;
import com.stormpath.sdk.servlet.filter.FilterChainManager;

import javax.servlet.ServletContext;

/**
 * @since 1.0.0
 */
public class FilterChainManagerFactory extends ConfigSingletonFactory<FilterChainManager> {

    public static final String FILTER_CONFIG_PREFIX = "stormpath.web.filters.";

    @Override
    protected FilterChainManager createInstance(ServletContext servletContext) throws Exception {

        DefaultFilterChainManager mgr = new DefaultFilterChainManager(servletContext);

        //add the defaults:
        for (DefaultFilter defaultFilter : DefaultFilter.values()) {
            Object o = defaultFilter.getFactoryClass();
            if (o == null) {
                o = defaultFilter.getFilterClass();
            }
            mgr.addFilter(defaultFilter.name(), o);
        }

        //pick up any user-configured filterish things as well as allow them to override the defaults:
        Config config = getConfig();

        for (String key : config.keySet()) {

            if (key.startsWith(FILTER_CONFIG_PREFIX)) {

                String instanceName = key.substring(FILTER_CONFIG_PREFIX.length());

                //if there are any periods in the remainder, then the property is not a class name - it is an
                // instance-specific config property, so just ignore it:
                int i = instanceName.indexOf('.');
                if (i >= 0) {
                    continue;
                }

                String className = config.getInstance(key);
                Object instance = Classes.newInstance(className);
                mgr.addFilter(instanceName, instance);
            }
        }

        return new DefaultFilterChainManagerConfigurer(mgr, servletContext, config).configure();
    }
}
