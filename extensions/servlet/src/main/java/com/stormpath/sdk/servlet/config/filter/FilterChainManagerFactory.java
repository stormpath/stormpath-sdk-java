package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.config.ImplementationClassResolver;
import com.stormpath.sdk.servlet.filter.DefaultFilter;
import com.stormpath.sdk.servlet.filter.DefaultFilterChainManager;
import com.stormpath.sdk.servlet.filter.FilterChainManager;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class FilterChainManagerFactory extends ConfigSingletonFactory<FilterChainManager> {

    public static final String FILTER_CONFIG_PREFIX = "stormpath.web.filters.";

    @Override
    protected FilterChainManager createInstance(ServletContext servletContext) throws Exception {

        DefaultFilterChainManager mgr = new DefaultFilterChainManager(servletContext);

        Map<String, Class<? extends Filter>> configuredFilterClasses = new LinkedHashMap<>();

        //add the defaults:
        for (DefaultFilter defaultFilter : DefaultFilter.values()) {
            configuredFilterClasses.put(defaultFilter.name(), defaultFilter.getFilterClass());
        }

        Config config = getConfig();

        //pick up any user-configured filter classes and allow them to override the defaults:
        Map<String, Class<Filter>> foundClasses =
            new ImplementationClassResolver<>(config, FILTER_CONFIG_PREFIX, Filter.class).findImplementationClasses();

        if (!com.stormpath.sdk.lang.Collections.isEmpty(foundClasses)) {
            configuredFilterClasses.putAll(foundClasses);
        }

        mgr.addFilterClasses(configuredFilterClasses);

        new DefaultFilterChainManagerConfigurer(mgr, servletContext, config).configure();

        return mgr;
    }
}
