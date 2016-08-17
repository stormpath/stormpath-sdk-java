package com.stormpath.zuul.account;

import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.zuul.filter.AppliedRequestHeaderFilter;

/**
 * @since 1.1.0
 */
public class ForwardedAccountHeaderFilter extends AppliedRequestHeaderFilter {

    public static final String DEFAULT_HEADER_NAME = "X-Forwarded-Account";

    private AccountResolver accountResolver;

    public ForwardedAccountHeaderFilter() {
        setHeaderName(DEFAULT_HEADER_NAME);
        AccountResolver accountResolver = new DefaultAccountResolver();
        setAccountResolver(accountResolver);
        DefaultAccountHeaderValueResolver resolver = new DefaultAccountHeaderValueResolver();
        resolver.setAccountResolver(accountResolver);
        setValueResolver(resolver);
    }

    public void setAccountResolver(AccountResolver accountResolver) {
        this.accountResolver = accountResolver;
    }

    @Override
    public boolean shouldFilter() {
        return accountResolver.hasAccount(getRequestContext().getRequest());
    }
}
