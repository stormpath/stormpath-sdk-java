/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.organization;

import com.stormpath.sdk.impl.query.DefaultCriteria;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingOptions;

/**
 * @since 1.0.RC7
 */
public class DefaultOrganizationAccountStoreMappingCriteria extends DefaultCriteria<OrganizationAccountStoreMappingCriteria, OrganizationAccountStoreMappingOptions> implements OrganizationAccountStoreMappingCriteria {

    public DefaultOrganizationAccountStoreMappingCriteria() {
        super(new DefaultOrganizationAccountStoreMappingOptions());
    }

    @Override
    public OrganizationAccountStoreMappingCriteria orderByListIndex() {
        return orderBy(DefaultOrganizationAccountStoreMapping.LIST_INDEX);
    }

    @Override
    public OrganizationAccountStoreMappingCriteria withOrganization() {
        getOptions().withOrganization();
        return this;
    }

    @Override
    public OrganizationAccountStoreMappingCriteria withAccountStore() {
        getOptions().withAccountStore();
        return this;
    }
}
