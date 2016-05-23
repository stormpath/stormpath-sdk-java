package com.stormpath.sdk.servlet.oauth.impl;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.oauth.OAuthTokenResolver;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @since 1.0.0
 */
public abstract class AbstractOAuthTokenResolver implements OAuthTokenResolver {

    protected abstract String getRequestAttributeName();

    @Override
    public boolean hasToken(ServletRequest request) {
        Assert.notNull(request, "ServletRequest argument cannot be null.");
        String token = findToken(request);
        return token != null;
    }

    @Override
    public String getToken(ServletRequest request) {
        Assert.notNull(request, "ServletRequest argument cannot be null.");
        return findToken(request);
    }

    @Override
    public String getRequiredToken(ServletRequest request) throws IllegalArgumentException {
        Assert.notNull(request, "ServletRequest argument cannot be null.");
        String token = findToken(request);
        Assert.notNull(token, "The current request does not reflect an authenticated user.  " +
                "Call 'hasToken' to check if an authenticated user OAuth token exists before " +
                "calling this method.");
        return token;
    }

    protected String findToken(ServletRequest request) {
        Object value = request.getAttribute(getRequestAttributeName());
        if (value == null) {
            //Should we look in the session?
            Assert.isInstanceOf(HttpServletRequest.class, request, "Only HttpServletRequests are supported.");
            HttpServletRequest req = (HttpServletRequest) request;
            HttpSession session = req.getSession(false);
            if (session != null) {
                value = session.getAttribute(getRequestAttributeName());
            }
        }
        if (value == null) {
            return null;
        }

        Assert.isInstanceOf(String.class, value,
                getRequestAttributeName() + " attribute must be a " + String.class.getName() + " instance.");

        return (String) value;
    }
}
