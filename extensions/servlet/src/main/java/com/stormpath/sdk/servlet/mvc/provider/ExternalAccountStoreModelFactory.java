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
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.provider.GoogleProvider;
import com.stormpath.sdk.provider.OAuthProvider;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.provider.saml.SamlProvider;
import com.stormpath.sdk.servlet.application.ApplicationResolver;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Returns {@link AccountStoreModel} instances <em>only</em> for external account stores mapped to the current
 * application.  An <em>external</em> account store is a 3rd-party account store like Google, LinkedIn or SAML where
 * the accounts are not natively resolved by the application and rely on the end-user to indicate which account service
 * to use.
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

        AccountStoreModelVisitor visitor = new AccountStoreModelVisitor(accountStores, getAuthorizeBaseUri(request));

        for (ApplicationAccountStoreMapping mapping : mappings) {

            final AccountStore accountStore = mapping.getAccountStore();

            accountStore.accept(visitor);
        }

        return visitor.getAccountStores();
    }

    private String getAuthorizeBaseUri(HttpServletRequest request) {
        String authorizeBaseUri = null;
        try {
            authorizeBaseUri = new URI(request.getScheme(), null, request.getServerName(), request.getServerPort(), null, null, null).toString();
        } catch (URISyntaxException e) {
            // should never happen
            Assert.isTrue(false, "Getting the base URI from " + request.toString() + " caused URISyntaxException: " + e.getMessage());
        }
        return authorizeBaseUri;
    }

    private class AccountStoreModelVisitor implements AccountStoreVisitor {

        private final List<AccountStoreModel> accountStores;
        private final String authorizeBaseUri;

        public AccountStoreModelVisitor(List<AccountStoreModel> accountStores, String authorizeBaseUri) {
            this.accountStores = accountStores;
            this.authorizeBaseUri = authorizeBaseUri;
        }

        @Override
        public void visit(Group group) {
        }

        //Only directories can support provider-based workflows:
        @Override
        public void visit(Directory directory) {

            Provider provider = directory.getProvider();
            ProviderModel providerModel = null;

            if (provider instanceof GoogleProvider) {
                providerModel = new GoogleOAuthProviderModel((GoogleProvider) provider);
            }
            else if (provider instanceof OAuthProvider) {
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

        @Override
        public void visit(Organization organization) {
        }

        public List<AccountStoreModel> getAccountStores() {
            return accountStores;
        }
    }
}
