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

import com.stormpath.sdk.accountStoreMapping.AccountStoreMapping;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingCriteria;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList;
import com.stormpath.sdk.application.ApplicationAccountStoreMappings;
import com.stormpath.sdk.application.webconfig.ApplicationWebConfig;
import com.stormpath.sdk.application.webconfig.ApplicationWebConfigStatus;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingList;
import com.stormpath.sdk.provider.GoogleProvider;
import com.stormpath.sdk.provider.OAuthProvider;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.provider.saml.SamlProvider;
import com.stormpath.sdk.resource.CollectionResource;
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
        CollectionResource<? extends AccountStoreMapping> accountStoreMappings;

        String onk = request.getParameter("organizationNameKey");
        if (Strings.hasText(onk)) {
            accountStoreMappings = getOrganizationAccountStoreMappings(app, onk);
        } else {
            accountStoreMappings = getApplicationAccountStoreMappings(app);
        }

        if (accountStoreMappings == null) {
            return new ArrayList<>(); //maybe error if onk isn't found???
        }

        final List<AccountStoreModel> accountStores = new ArrayList<>(accountStoreMappings.getSize());

        AccountStoreModelVisitor visitor =
                new AccountStoreModelVisitor(accountStores, getAuthorizeBaseUri(request, app.getWebConfig()));

        for (AccountStoreMapping mapping : accountStoreMappings) {

            final AccountStore accountStore = mapping.getAccountStore();

            accountStore.accept(visitor);
        }

        return visitor.getAccountStores();
    }

    private ApplicationAccountStoreMappingList getApplicationAccountStoreMappings(Application app) {
        int pageSize = 100; //get as much as we can in a single request
        ApplicationAccountStoreMappingCriteria criteria = ApplicationAccountStoreMappings.criteria().limitTo(pageSize);
        return app.getAccountStoreMappings(criteria);
    }

    private OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings(Application app, final String nameKey) {
        ApplicationAccountStoreMappingList accountStoreMappings = app.getAccountStoreMappings();

        final Organization[] organization = new Organization[1];

        for (final AccountStoreMapping accountStoreMapping : accountStoreMappings) {
            AccountStore accountStore = accountStoreMapping.getAccountStore();
            accountStore.accept(new AccountStoreVisitor() {
                @Override
                public void visit(Group group) {}

                @Override
                public void visit(Directory directory) {}

                @Override
                public void visit(Organization org) {
                    if (org.getNameKey().equals(nameKey)) {
                         organization[0] = org;
                    }
                }
            });
        }

        if (organization[0] == null) {
            return null;
        }

        return organization[0].getAccountStoreMappings();
    }

    @SuppressWarnings("WeakerAccess") // Want to allow overriding this method
    protected String getAuthorizeBaseUri(@SuppressWarnings("UnusedParameters") HttpServletRequest request, ApplicationWebConfig webConfig) {
        String authorizeBaseUri = null;
        if (webConfig.getStatus() == ApplicationWebConfigStatus.ENABLED && webConfig.getLogin().isEnabled()) {
            authorizeBaseUri = "https://" + webConfig.getDomainName();
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
            //Do nothing... groups cannot be external
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

        @Override
        public void visit(Organization organization) {
            //Do nothing... organizations cannot be external
        }

        public List<AccountStoreModel> getAccountStores() {
            return accountStores;
        }
    }
}
