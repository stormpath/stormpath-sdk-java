package com.stormpath.sdk.servlet.filter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.0
 */
public class DefaultFilterChainResolver implements FilterChainResolver {

    @Override
    public FilterChain getChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        return chain;
    }
}
