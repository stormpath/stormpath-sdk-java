package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.FilterChainManager;
import com.stormpath.sdk.servlet.filter.FilterChainResolver;
import com.stormpath.sdk.servlet.filter.PathMatchingFilterChainResolver;
import com.stormpath.sdk.servlet.filter.PrioritizedFilterChainResolver;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.0
 */
public class FilterChainResolverFactory extends ConfigSingletonFactory<FilterChainResolver> {

    @Override
    protected FilterChainResolver createInstance(ServletContext servletContext) throws Exception {

        PathMatchingFilterChainResolver resolver = new PathMatchingFilterChainResolver(servletContext);
        FilterChainManager mgr = getConfig().getFilterChainManager();
        resolver.setFilterChainManager(mgr);

        //ensure the always-on AccountResolverFilter is available:
        AccountResolverFilterFactory factory = new AccountResolverFilterFactory();
        factory.init(servletContext);
        Filter accountFilter = factory.getInstance();
        final List<Filter> priorityFilters = Collections.singletonList(accountFilter);

        //we always want our immediateExecutionFilters to run before the default chain:
        return new PrioritizedFilterChainResolver(resolver, priorityFilters);
    }
}
