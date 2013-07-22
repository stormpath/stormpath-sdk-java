package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.application.AccountStoreMapping;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.IntegerProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;

import java.util.Map;

/**
 * @since 0.9
 */
public class DefaultAccountStoreMapping extends AbstractInstanceResource implements AccountStoreMapping {

    // SIMPLE PROPERTIES:
    static final IntegerProperty LIST_INDEX = new IntegerProperty("listIndex", false);
    static final BooleanProperty NEW_ACCOUNT_STORE = new BooleanProperty("isNewAccountStore", false);
    static final BooleanProperty NEW_GROUP_STORE = new BooleanProperty("isNewGroupStore", false);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Application> APPLICATION = new ResourceReference<Application>("application", Application.class, true);
    static final ResourceReference<AccountStore> ACCOUNT_STORE = new ResourceReference<AccountStore>("accountStore", AccountStore.class, true);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            LIST_INDEX, NEW_ACCOUNT_STORE, NEW_GROUP_STORE, APPLICATION, ACCOUNT_STORE);

    public DefaultAccountStoreMapping(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAccountStoreMapping(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public Application getApplication() {
        return getResourceProperty(APPLICATION);
    }

    @Override
    public void setApplication(Application application) {
        setResourceProperty(APPLICATION, application);
    }

    @Override
    public AccountStore getAccountStore() {
        //TODO: IMPLEMENT
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAccountStore(AccountStore accountStore) {
        //TODO: IMPLEMENT
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getListIndex() {
        //TODO: IMPLEMENT
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setListIndex() {
        //TODO: IMPLEMENT
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isNewAccountStore() {
        //TODO: IMPLEMENT
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setNewAccountStore(boolean newAccountStore) {
        //TODO: IMPLEMENT
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setNewGroupStore(boolean newGroupStore) {
        //TODO: IMPLEMENT
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isNewGroupStore() {
        //TODO: IMPLEMENT
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete() {
        //TODO: IMPLEMENT
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
