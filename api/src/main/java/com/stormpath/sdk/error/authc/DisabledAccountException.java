package com.stormpath.sdk.error.authc;

import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.resource.ResourceException;

/**
 * DisabledAccountException
 *
 * @since 1.0.RC
 */
public class DisabledAccountException extends ResourceException {

    private final AccountStatus accountStatus;

    public DisabledAccountException(Error error, AccountStatus accountStatus) {
        super(error);
        this.accountStatus = accountStatus;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }
}
