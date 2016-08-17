package com.stormpath.zuul.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Function;

import java.util.Map;

/**
 * @since 1.1.0
 */
public class AccountGroupsByMapCriteriaFunction implements Function<Account, Iterable<Group>> {

    private Map<String, Object> criteria;

    public AccountGroupsByMapCriteriaFunction(Map<String, Object> criteria) {
        Assert.notEmpty(criteria, "criteria cannot be null or empty.");
        this.criteria = criteria;
    }

    @Override
    public Iterable<Group> apply(Account account) {
        return account.getGroups(this.criteria);
    }

}
