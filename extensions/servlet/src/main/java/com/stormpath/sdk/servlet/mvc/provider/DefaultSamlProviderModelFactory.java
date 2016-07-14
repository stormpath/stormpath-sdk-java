/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationAccountStoreMapping;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingCriteria;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList;
import com.stormpath.sdk.application.ApplicationAccountStoreMappings;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.provider.saml.SamlProvider;
import com.stormpath.sdk.servlet.application.ApplicationResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class DefaultSamlProviderModelFactory implements AccountStoreModelFactory {

    @Override
    public List<AccountStoreModel> getAccountStores(HttpServletRequest request) {

        Application app = ApplicationResolver.INSTANCE.getApplication(request);

        int pageSize = 100; //get as much as we can in a single request
        ApplicationAccountStoreMappingCriteria criteria = ApplicationAccountStoreMappings.criteria().limitTo(pageSize);
        ApplicationAccountStoreMappingList mappings = app.getApplicationAccountStoreMappings(criteria);

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

            if (provider instanceof SamlProvider) {
                SamlProvider samlProvider = (SamlProvider) provider;
                ProviderModel providerModel = new DefaultSamlProviderModel(samlProvider);
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