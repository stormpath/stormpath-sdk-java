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
import com.stormpath.sdk.organization.OrganizationCriteria;
import com.stormpath.sdk.organization.OrganizationOptions;

/**
 * @since 1.0.RC5
 */
public class DefaultOrganizationCriteria extends DefaultCriteria<OrganizationCriteria, OrganizationOptions> implements OrganizationCriteria {

    public DefaultOrganizationCriteria() {
        super(new DefaultOrganizationOptions());
    }

    @Override
    public OrganizationCriteria orderByName() {
        return orderBy(DefaultOrganization.NAME);
    }

    @Override
    public OrganizationCriteria orderByDescription() {
        return orderBy(DefaultOrganization.DESCRIPTION);
    }

    @Override
    public OrganizationCriteria orderByStatus() {
        return orderBy(DefaultOrganization.STATUS);
    }

    @Override
    public OrganizationCriteria withCustomData() {
        getOptions().withCustomData();
        return this;
    }

    @Override
    public OrganizationCriteria withTenant() {
        getOptions().withTenant();
        return this;
    }

    @Override
    public OrganizationCriteria withOrganizationAccountStoreMappings() {
        getOptions().withOrganizationAccountStoreMappings();
        return this;
    }

    @Override
    public OrganizationCriteria withOrganizationAccountStoreMappings(int limit) {
        getOptions().withOrganizationAccountStoreMappings(limit);
        return this;
    }

    @Override
    public OrganizationCriteria withOrganizationAccountStoreMappings(int limit, int offset) {
        getOptions().withOrganizationAccountStoreMappings(limit, offset);
        return this;
    }
}
