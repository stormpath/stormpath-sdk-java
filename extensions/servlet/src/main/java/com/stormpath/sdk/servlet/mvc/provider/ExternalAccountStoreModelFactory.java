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
import com.stormpath.sdk.application.webconfig.ApplicationWebConfig;
import com.stormpath.sdk.application.webconfig.ApplicationWebConfigStatus;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.AccountStoreVisitorAdapter;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.provider.GoogleProvider;
import com.stormpath.sdk.provider.OAuthProvider;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.provider.saml.SamlProvider;
import com.stormpath.sdk.servlet.application.ApplicationResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Returns {@link AccountStoreModel} instances <em>only</em> for external account stores mapped to the current
 * application.  An <em>external</em> account store is a 3rd-party account store like Google, LinkedIn or SAML where
 * the accounts are not natively resolved by the application and rely on the end-user to indicate which account service
 * to use.
 *
 * @since 1.0.0
 */
public class ExternalAccountStoreModelFactory implements AccountStoreModelFactory {

    @Override
    public List<AccountStoreModel> getAccountStores(HttpServletRequest request) {

        Application app = ApplicationResolver.INSTANCE.getApplication(request);

        int pageSize = 100; //get as much as we can in a single request
        ApplicationAccountStoreMappingCriteria criteria = ApplicationAccountStoreMappings.criteria().limitTo(pageSize);
        ApplicationAccountStoreMappingList mappings = app.getAccountStoreMappings(criteria);

        final List<AccountStoreModel> accountStores = new ArrayList<>(mappings.getSize());

        AccountStoreModelVisitor visitor =
                new AccountStoreModelVisitor(accountStores, getAuthorizeBaseUri(request, app.getWebConfig()));

        for (ApplicationAccountStoreMapping mapping : mappings) {

            final AccountStore accountStore = mapping.getAccountStore();

            accountStore.accept(visitor);
        }

        return visitor.getAccountStores();
    }

    @SuppressWarnings("WeakerAccess") // Want to allow overriding this method
    protected String getAuthorizeBaseUri(@SuppressWarnings("UnusedParameters") HttpServletRequest request, ApplicationWebConfig webConfig) {
        String authorizeBaseUri = null;
        if (webConfig.getStatus() == ApplicationWebConfigStatus.ENABLED && webConfig.getLogin().isEnabled()) {
            authorizeBaseUri = "https://" + webConfig.getDomainName();
        }
        return authorizeBaseUri;
    }

    private class AccountStoreModelVisitor extends AccountStoreVisitorAdapter {

        private final List<AccountStoreModel> accountStores;
        private final String authorizeBaseUri;

        public AccountStoreModelVisitor(List<AccountStoreModel> accountStores, String authorizeBaseUri) {
            this.accountStores = accountStores;
            this.authorizeBaseUri = authorizeBaseUri;
        }

        //Only directories can support provider-based workflows:
        @Override
        public void visit(Directory directory) {

            Provider provider = directory.getProvider();
            ProviderModel providerModel = null;

            if (provider instanceof GoogleProvider) {
                providerModel = new GoogleOAuthProviderModel((GoogleProvider) provider);
            } else if (provider instanceof OAuthProvider) {
                providerModel = new DefaultOAuthProviderModel((OAuthProvider) provider);
            } else if (provider instanceof SamlProvider) {
                //We currently don't need to retain any SAML-specific values for the login model, so we
                //just use a simple provider model here:
                providerModel = new DefaultProviderModel(provider);
            }

            if (providerModel != null) {
                AccountStoreModel accountStoreModel = new DefaultAccountStoreModel(directory, providerModel, authorizeBaseUri);
                accountStores.add(accountStoreModel);
            }
        }

        public List<AccountStoreModel> getAccountStores() {
            return accountStores;
        }
    }
}
