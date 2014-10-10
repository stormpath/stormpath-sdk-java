package com.stormpath.sdk.servlet.account;

import com.stormpath.sdk.account.Account;

import javax.servlet.ServletRequest;

public interface RequestAccountResolver {

    /**
     * A thread-safe instance to use as desired.  The implementation is a
     * {@link com.stormpath.sdk.servlet.account.DefaultRequestAccountResolver DefaultRequestAccountResolver}.
     */
    public static final RequestAccountResolver INSTANCE = new DefaultRequestAccountResolver();

    boolean hasAccount(ServletRequest request);

    Account getAccount(ServletRequest request);
}
