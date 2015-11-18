/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.impl.organization

import com.stormpath.sdk.organization.Organization
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertSame
import static org.testng.Assert.fail

/**
 * @since 1.0.RC7
 */
class DefaultCreateOrganizationRequestTest {

    @Test
    void testAll() {

        def org = createStrictMock(Organization)
        def request = new DefaultCreateOrganizationRequest(org)

        assertSame(request.organization, org)

        request.accept(new CreateOrganizationRequestVisitor() {
            @Override
            void visit(DefaultCreateOrganizationRequest defaultRequest) {
                assertSame(defaultRequest, request)
            }

            @Override
            void visit(CreateOrganizationAndDirectoryRequest createOrganizationAndDirectoryRequest) {
                fail("shouldn't have received a " + createOrganizationAndDirectoryRequest.class.name)
            }
        })
    }
}
