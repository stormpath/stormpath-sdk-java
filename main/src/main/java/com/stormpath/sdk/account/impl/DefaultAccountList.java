package com.stormpath.sdk.account.impl;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.resource.impl.AbstractCollectionResource;

import java.util.Map;

/**
 * @author Jeff Wysong
 *         Date: 6/12/12
 *         Time: 6:08 PM
 */
public class DefaultAccountList extends AbstractCollectionResource<Account> implements AccountList {

    public DefaultAccountList(DataStore dataStore) {
        super(dataStore);
    }

    public DefaultAccountList(DataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    protected Class<Account> getItemType() {
        return Account.class;
    }
}
