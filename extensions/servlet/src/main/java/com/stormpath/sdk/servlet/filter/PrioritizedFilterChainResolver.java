package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @since 1.0.0
 */
public class PrioritizedFilterChainResolver implements FilterChainResolver {

    private final FilterChainResolver delegate;
    private final List<Filter> priorityFilters;

    public PrioritizedFilterChainResolver(FilterChainResolver delegate, List<Filter> priorityFilters) {
        Assert.notNull(delegate, "Delegate FilterChainResolver cannot be null.");
        this.delegate = delegate;
        this.priorityFilters = priorityFilters;
    }

    @Override
    public FilterChain getChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

        FilterChain target = delegate.getChain(request, response, chain);
        if (target == null) {
            target = chain;
        }

        if (Collections.isEmpty(priorityFilters)) {
            return target;
        }

        return new ProxiedFilterChain(target, priorityFilters);
    }
}
