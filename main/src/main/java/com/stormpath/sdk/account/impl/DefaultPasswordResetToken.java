package com.stormpath.sdk.account.impl;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.PasswordResetToken;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.resource.impl.AbstractResource;

import java.util.Map;

/**
 * @author Jeff Wysong
 *         Date: 6/13/12
 *         Time: 2:27 PM
 * @since 0.2
 */
public class DefaultPasswordResetToken extends AbstractResource implements PasswordResetToken {

    private final String EMAIL = "email";
    private final String ACCOUNT = "account";

    protected DefaultPasswordResetToken(DataStore dataStore) {
        super(dataStore);
    }

    protected DefaultPasswordResetToken(DataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public String getEmail() {
        return getStringProperty(EMAIL);
    }

    @Override
    public void setEmail(String email) {
        setProperty(EMAIL, email);
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT, Account.class);
    }
}
