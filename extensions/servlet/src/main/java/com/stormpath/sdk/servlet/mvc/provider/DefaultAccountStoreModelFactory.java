package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.application.*;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.provider.OauthProvider;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.servlet.application.ApplicationResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 1.0.RC8
 */
public class DefaultAccountStoreModelFactory implements AccountStoreModelFactory {

    @Override
    public List<AccountStoreModel> getAccountStores(HttpServletRequest request) {

        Application app = ApplicationResolver.INSTANCE.getApplication(request);

        int pageSize = 100; //get as much as we can in a single request
        ApplicationAccountStoreMappingCriteria criteria = ApplicationAccountStoreMappings.criteria().limitTo(pageSize);
        ApplicationAccountStoreMappingList mappings = app.getAccountStoreMappings(criteria);

        final List<AccountStoreModel> accountStores = new ArrayList<AccountStoreModel>(mappings.getSize());

        AccountStoreModelVisitor visitor = new AccountStoreModelVisitor(accountStores);

        for (ApplicationAccountStoreMapping mapping : mappings) {

            final AccountStore accountStore = mapping.getAccountStore();

            accountStore.accept(visitor);
        }

        return visitor.getAccountStores();
    }

    private class AccountStoreModelVisitor implements AccountStoreVisitor {

        private final List<AccountStoreModel> accountStores;

        public AccountStoreModelVisitor(List<AccountStoreModel> accountStores) {
            this.accountStores = accountStores;
        }

        @Override
        public void visit(Group group) {
        }

        //Only directories can support provider-based workflows:
        @Override
        public void visit(Directory directory) {

            Provider provider = directory.getProvider();

            if (provider instanceof OauthProvider) {
                OauthProvider oauthProvider = (OauthProvider) provider;
                ProviderModel providerModel = new DefaultOauthProviderModel(oauthProvider);
                AccountStoreModel acctStoreModel = new DefaultAccountStoreModel(directory, providerModel);
                accountStores.add(acctStoreModel);
            }
        }

        @Override
        public void visit(Organization organization) {
        }

        public List<AccountStoreModel> getAccountStores() {
            return accountStores;
        }
    }
}
