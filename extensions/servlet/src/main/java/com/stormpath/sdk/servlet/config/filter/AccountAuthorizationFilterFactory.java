package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.account.AccountAuthorizationFilter;

/**
 * @since 1.0.0
 */
public class AccountAuthorizationFilterFactory extends AccessControlFilterFactory<AccountAuthorizationFilter> {

    @Override
    protected AccountAuthorizationFilter newInstance() {
        return new AccountAuthorizationFilter();
    }

    @Override
    protected void configure(AccountAuthorizationFilter f, Config config) {
        //currently a no-op
    }
}
