package com.stormpath.zuul.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Function;

/**
 * @since 1.1.0
 */
public class AccountGroupsByCriteriaFunction implements Function<Account, Iterable<Group>> {

    private GroupCriteria criteria;

    public AccountGroupsByCriteriaFunction(GroupCriteria criteria) {
        Assert.notNull(criteria, "criteria cannot be null.");
        this.criteria = criteria;
    }

    @Override
    public Iterable<Group> apply(Account account) {
        return account.getGroups(this.criteria);
    }
}
