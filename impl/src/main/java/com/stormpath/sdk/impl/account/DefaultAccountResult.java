package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.idsite.AccountResult;
import com.stormpath.sdk.idsite.IdSiteResultStatus;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 * DefaultAccountResult is the default implementation of the {@link AccountResult AccountResult} interface.
 *
 * @since 1.0.RC
 */
public class DefaultAccountResult extends AbstractResource implements AccountResult {

    // SIMPLE PROPERTIES
    public  static final StringProperty STATE = new StringProperty("state");

    public static final BooleanProperty NEW_ACCOUNT = new BooleanProperty("isNewAccount");

    // INSTANCE RESOURCE REFERENCES:
    public static final ResourceReference<Account> ACCOUNT = new ResourceReference<Account>("account", Account.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(NEW_ACCOUNT, ACCOUNT);

    private IdSiteResultStatus status;

    public DefaultAccountResult(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAccountResult(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT);
    }

    @Override
    public boolean isNewAccount() {
        return getBoolean(NEW_ACCOUNT);
    }

    @Override
    public String getState() {
        return getString(STATE);
    }

    @Override
    public IdSiteResultStatus getStatus() {
        return status;
    }
    
    @Override
    public void setStatus(IdSiteResultStatus status) {
        this.status = status;
    }
}
