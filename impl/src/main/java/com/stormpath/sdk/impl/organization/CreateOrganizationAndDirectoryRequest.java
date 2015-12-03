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

import com.stormpath.sdk.organization.Organization;

/**
 * @since 1.0.RC7
 */
public class CreateOrganizationAndDirectoryRequest extends DefaultCreateOrganizationRequest {

    private final String directoryName;

    public CreateOrganizationAndDirectoryRequest(Organization organization, String directoryName) {
        super(organization);
        this.directoryName = directoryName; //can be null if the directory should be auto-named
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    @Override
    public void accept(CreateOrganizationRequestVisitor visitor) {
        visitor.visit(this);
    }
}
