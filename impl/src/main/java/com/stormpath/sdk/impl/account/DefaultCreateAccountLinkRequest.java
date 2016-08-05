package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.AccountLink;
import com.stormpath.sdk.account.AccountLinkOptions;
import com.stormpath.sdk.account.CreateAccountLinkRequest;
import com.stormpath.sdk.lang.Assert;

public class DefaultCreateAccountLinkRequest implements CreateAccountLinkRequest{

    private AccountLink accountLink;//TODO : Is this required? Should this be final?

    private final String leftAccountHref;

    private final String rightAccountHref;

    private final AccountLinkOptions options;

    public DefaultCreateAccountLinkRequest(String leftAccountHref, String rightAccountHref, AccountLinkOptions options) {
        Assert.hasText(leftAccountHref, "leftAccountHref cannot be null.");
        Assert.hasText(rightAccountHref, "rightAccountHref cannot be null.");
        this.leftAccountHref = accountLink.getLeftAccount().getHref();
        this.rightAccountHref = accountLink.getRightAccount().getHref();
        this.options = options;
    }

    @Override
    public AccountLink getAccountLink() {
        return this.accountLink;
    }

    @Override
    public String getLeftAccountHref() {
        return this.leftAccountHref;
    }

    @Override
    public String getRightAccountHref() {
        return this.rightAccountHref;
    }

    @Override
    public boolean isAccountLinkOptionsSpecified() {
        return false;
    }

    @Override
    public AccountLinkOptions getAccountLinkOptions() throws IllegalStateException {
        if (this.options == null) {
            throw new IllegalStateException("accountLinkOptions has not been configured. Use the isAccountLinkOptionsSpecified() method to check first before invoking this method.");
        }
        return this.options;
    }
}
