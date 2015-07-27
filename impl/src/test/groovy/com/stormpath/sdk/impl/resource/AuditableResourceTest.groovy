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
package com.stormpath.sdk.impl.resource

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.Test

import java.text.DateFormat

import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.replay
import static org.testng.Assert.assertEquals

/**
 * @since 1.0.RC4.6
 */
class AuditableResourceTest {

    @Test
    void testDefault() {

        DateFormat dateFormatter = new ISO8601DateFormat();

        InternalDataStore ds = createStrictMock(InternalDataStore)
        replay ds

        def properties = ['href': 'http://test.com/values/123',
                          'createdAt': '2014-04-18T21:32:19.651Z',
                          'modifiedAt': '2014-06-19T20:48:48.000Z']

        AuditableTestResource resource = new AuditableTestResource(ds, properties)

        assertEquals resource.getHref(), properties.href
        assertEquals dateFormatter.parse(properties.createdAt), resource.getCreatedAt()
        assertEquals dateFormatter.parse(properties.modifiedAt), resource.getModifiedAt()
    }
}
