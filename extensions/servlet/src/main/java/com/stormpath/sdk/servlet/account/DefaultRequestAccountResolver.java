package com.stormpath.sdk.servlet.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.ServletRequest;

public class DefaultRequestAccountResolver implements RequestAccountResolver {

    public static final String REQUEST_ATTR_NAME = Account.class.getName();

    @Override
    public boolean hasAccount(ServletRequest request) {
        Assert.notNull(request, "ServletRequest argument cannot be null.");
        Object value = request.getAttribute(REQUEST_ATTR_NAME);
        return value != null && value instanceof Account;
    }

    @Override
    public Account getAccount(ServletRequest request) {
        Assert.notNull(request, "ServletRequest argument cannot be null.");
        Assert.isTrue(hasAccount(request), "The current request does not reflect an authenticated user.  " +
                                           "Call 'hasAccount' to check if an authenticated user exists before " +
                                           "calling this method.");

        Object value = request.getAttribute(REQUEST_ATTR_NAME);
        //we can do a direct cast here because we already check for type in the hasAccount call above:
        return (Account)value;
    }
}
