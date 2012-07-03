package com.stormpath.sdk.account.impl;

import com.stormpath.sdk.account.EmailVerificationToken;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.resource.impl.AbstractResource;

import java.util.Map;

/**
 * @author Jeff Wysong
 *         Date: 7/3/12
 *         Time: 11:54 AM
 * @since 0.2
 */
public class DefaultEmailVerificationToken extends AbstractResource implements EmailVerificationToken {

    public DefaultEmailVerificationToken(DataStore dataStore) {
        super(dataStore);
    }

    public DefaultEmailVerificationToken(DataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }
}
