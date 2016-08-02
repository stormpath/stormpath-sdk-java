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
package com.stormpath.sdk.servlet.tenant;

import com.stormpath.sdk.accountStoreMapping.AccountStoreMapping;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.0
 */
public class DefaultTenantResolver implements TenantResolver<Organization> {

    Resolver<String> organizationNameKeyResolver;
    ApplicationResolver applicationResolver = ApplicationResolver.INSTANCE;

    public void setOrganizationNameKeyResolver(Resolver<String> organizationNameKeyResolver) {
        this.organizationNameKeyResolver = organizationNameKeyResolver;
    }

    public Organization get(HttpServletRequest request, HttpServletResponse response) {

        final String domainName = organizationNameKeyResolver.get(request, response);
        if (domainName == null) {
            return null;
        }

        Application application = applicationResolver.getApplication(request);
        ApplicationAccountStoreMappingList accountStoreMappings = application.getAccountStoreMappings();
        final Organization organization[] = {null};
        for (AccountStoreMapping accountStoreMapping : accountStoreMappings) {
            final AccountStore accountStore = accountStoreMapping.getAccountStore();

            accountStore.accept(new AccountStoreVisitor() {
                @Override
                public void visit(Group group) {
                    //no-op
                }

                @Override
                public void visit(Directory directory) {
                    //no-op
                }

                @Override
                public void visit(Organization org) {
                    if (domainName.equals(org.getName())) {
                        organization[0] = org;
                    }
                }
            });

            if (organization[0] != null) break;
        }
        return organization[0];
    }

}
