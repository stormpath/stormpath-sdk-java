package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.AccountLinkCriteria;
import com.stormpath.sdk.account.AccountLinkOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * @since 1.1.0
 */
public class DefaultAccountLinkCriteria extends DefaultCriteria<AccountLinkCriteria, AccountLinkOptions> implements AccountLinkCriteria {

    public DefaultAccountLinkCriteria() {
        super(new DefaultAccountLinkOptions());
    }

    @Override
    public AccountLinkCriteria orderByCreatedAt() {
        return orderBy(DefaultAccountLink.CREATED_AT);
    }

    @Override
    public AccountLinkCriteria withLeftAccount() {
        getOptions().withLeftAccount();
        return this;
    }

    @Override
    public AccountLinkCriteria withRightAccount() {
        getOptions().withRightAccount();
        return this;
    }
}
