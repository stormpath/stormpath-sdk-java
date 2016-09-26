package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.AccountLinkOptions;
import com.stormpath.sdk.impl.query.DefaultOptions;

/**
 * @since 1.1.0
 */
public class DefaultAccountLinkOptions extends DefaultOptions<AccountLinkOptions> implements AccountLinkOptions<AccountLinkOptions> {

    @Override
    public AccountLinkOptions withLeftAccount() {
        return expand(DefaultAccountLink.LEFT_ACCOUNT);
    }

    @Override
    public AccountLinkOptions withRightAccount() {
        return expand(DefaultAccountLink.RIGHT_ACCOUNT);
    }
}
