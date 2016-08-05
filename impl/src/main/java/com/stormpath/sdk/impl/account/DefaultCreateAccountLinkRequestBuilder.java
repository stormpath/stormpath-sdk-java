package com.stormpath.sdk.impl.account;


import com.stormpath.sdk.account.*;
import com.stormpath.sdk.lang.Assert;

public class DefaultCreateAccountLinkRequestBuilder implements CreateAccountLinkRequestBuilder {

    private String leftAccountHref;
    private String rightAccountHref;
    private AccountLinkOptions options;

    public DefaultCreateAccountLinkRequestBuilder(String leftAccountHref, String rightAccountHref) {
        Assert.notNull(leftAccountHref, "leftAccount cannot be null.");
        Assert.notNull(rightAccountHref, "rightAccount cannot be null.");
        this.leftAccountHref = leftAccountHref;
        this.rightAccountHref = rightAccountHref;
    }

    @Override
    public CreateAccountLinkRequestBuilder withLeftAccount(String leftAccountHref) {
        Assert.hasText(leftAccountHref, "leftAccountHref cannot be null.");
        this.leftAccountHref = leftAccountHref;
        return this;
    }

    @Override
    public CreateAccountLinkRequestBuilder withRightAccount(String rightAccountHref) {
        Assert.hasText(rightAccountHref, "rightAccountHref cannot be null.");
        this.rightAccountHref = rightAccountHref;
        return this;
    }

    @Override
    public CreateAccountLinkRequestBuilder withResponseOptions(AccountLinkOptions options) throws IllegalArgumentException {
        Assert.notNull(options);
        this.options = options;
        return this;
    }

    @Override
    public CreateAccountLinkRequest build() {
        return new DefaultCreateAccountLinkRequest(leftAccountHref,rightAccountHref,options);
    }

}
