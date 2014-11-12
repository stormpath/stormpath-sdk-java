package com.stormpath.sdk.servlet.account;

import com.stormpath.sdk.account.Account;

import javax.servlet.ServletRequest;

public interface AccountResolver {

    /**
     * A thread-safe instance to use as desired.  The implementation is a
     * {@link DefaultAccountResolver DefaultAccountResolver}.
     */
    public static final AccountResolver INSTANCE = new DefaultAccountResolver();

    boolean hasAccount(ServletRequest request);

    Account getAccount(ServletRequest request);
}
