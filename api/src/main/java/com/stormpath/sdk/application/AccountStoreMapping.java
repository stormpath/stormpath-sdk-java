package com.stormpath.sdk.application;

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * @since 0.9
 */
public interface AccountStoreMapping extends Resource, Saveable, Deletable {

    Application getApplication();

    void setApplication(Application application);

    AccountStore getAccountStore();

    void setAccountStore(AccountStore accountStore);

    int getListIndex();

    void setListIndex();

    boolean isNewAccountStore();

    void setNewAccountStore(boolean newAccountStore);

    void setNewGroupStore(boolean newGroupStore);

    boolean isNewGroupStore();
}
