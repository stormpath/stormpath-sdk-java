package com.stormpath.zuul.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.lang.Function;

/**
 * @since 1.1.0
 */
public class AccountGroupsFunction implements Function<Account, Iterable<Group>> {

    @Override
    public Iterable<Group> apply(Account account) {
        return account.getGroups();
    }
}
