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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.organization.CreateOrganizationRequest;
import com.stormpath.sdk.organization.Organization;

/**
 * @since 1.0.RC5.1
 */
public class DefaultCreateOrganizationRequest implements CreateOrganizationRequest {

    private final Organization organization;

    public DefaultCreateOrganizationRequest(Organization organization) {
        Assert.notNull(organization, "organization cannot be null.");
        this.organization = organization;
    }

    public Organization getOrganization(){
        return this.organization;
    }

    public void accept(CreateOrganizationRequestVisitor visitor) {
        visitor.visit(this);
    }
}
