package com.stormpath.sdk.servlet.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class DefaultAccountResolver implements AccountResolver {

    public static final String REQUEST_ATTR_NAME = "account";

    @Override
    public boolean hasAccount(ServletRequest request) {
        Assert.notNull(request, "ServletRequest argument cannot be null.");
        Account account = findAccount(request);
        return account != null;
    }

    protected Account findAccount(ServletRequest request) {
        Object value = request.getAttribute(REQUEST_ATTR_NAME);
        if (value == null) {
            Assert.isInstanceOf(HttpServletRequest.class, request, "Only HttpServletRequests are supported.");
            HttpServletRequest req = (HttpServletRequest) request;
            HttpSession session = req.getSession(false);
            if (session != null) {
                value = session.getAttribute(REQUEST_ATTR_NAME);
            }
        }
        if (value == null) {
            return null;
        }

        Assert.isInstanceOf(Account.class, value,
                            "Account attribute must be a " + Account.class.getName() + " instance.");
        return (Account) value;
    }

    @Override
    public Account getAccount(ServletRequest request) {
        Assert.notNull(request, "ServletRequest argument cannot be null.");
        Account account = findAccount(request);
        Assert.notNull(account, "The current request does not reflect an authenticated user.  " +
                                "Call 'hasAccount' to check if an authenticated user exists before " +
                                "calling this method.");
        return account;
    }
}
