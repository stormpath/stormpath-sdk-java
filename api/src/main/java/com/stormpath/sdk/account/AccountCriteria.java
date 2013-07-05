package com.stormpath.sdk.account;

import com.stormpath.sdk.query.Criteria;

/**
 * @since 0.8
 */
public interface AccountCriteria extends Criteria<AccountCriteria>, AccountOptions<AccountCriteria> {

    AccountCriteria orderByEmail();

    AccountCriteria orderByUsername();

    AccountCriteria orderByGivenName();

    AccountCriteria orderByMiddleName();

    AccountCriteria orderBySurname();

    AccountCriteria orderByStatus();
}
