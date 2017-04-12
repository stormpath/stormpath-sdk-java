package com.stormpath.sdk.impl.application.okta;

import com.stormpath.sdk.accountStoreMapping.AccountStoreMapping;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationAccountStoreMapping;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 *
 */
public class OktaApplicationAccountStoreMapping extends AbstractInstanceResource implements ApplicationAccountStoreMapping {

    private static final StringProperty ALGORITHM = new StringProperty("alg");
    private static final StringProperty TYPE = new StringProperty("kty");
    private static final StringProperty USE = new StringProperty("use");
    private static final StringProperty ID = new StringProperty("kid");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ALGORITHM);

    private Application application;

    private AccountStore accountStore;

    private int listIndex = 0;

    protected OktaApplicationAccountStoreMapping(InternalDataStore dataStore) {
        super(dataStore);
    }

    protected OktaApplicationAccountStoreMapping(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("save() is not implemented");
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("delete() is not implemented");
    }

    @Override
    public String getHref() {
        throw new UnsupportedOperationException("getHref() is not implemented");
    }

    @Override
    public AccountStore getAccountStore() {
        return accountStore;
    }

    @Override
    public AccountStoreMapping setAccountStore(AccountStore accountStore) {
        this.accountStore = accountStore;
        return this;
    }

    @Override
    public int getListIndex() {
        return listIndex;
    }

    @Override
    public AccountStoreMapping setListIndex(int listIndex) {
        this.listIndex = listIndex;
        return this;
    }

    @Override
    public boolean isDefaultAccountStore() {
        return false;
    }

    @Override
    public AccountStoreMapping setDefaultAccountStore(boolean defaultAccountStore) {
        throw new UnsupportedOperationException("setDefaultAccountStore() is not implemented");
    }

    @Override
    public boolean isDefaultGroupStore() {
        return false;
    }

    @Override
    public AccountStoreMapping setDefaultGroupStore(boolean defaultGroupStore) {
        throw new UnsupportedOperationException("setDefaultGroupStore() is not implemented");
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public ApplicationAccountStoreMapping setApplication(Application application) {
        this.application = application;
        return this;
    }
}
