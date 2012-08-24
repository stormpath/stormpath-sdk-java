/*
 * Copyright 2012 Stormpath, Inc.
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
package com.stormpath.sdk.impl.resource

import org.testng.annotations.Test
import com.stormpath.sdk.impl.ds.InternalDataStore
import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.4.1
 */
class AbstractResourceTest {

    /**
     * Asserts that the InternalDataStore is not invoked if the dirty properties can satisfy a getProperty request.
     */
    @Test
    void testNonMaterializedResourceGetDirtyPropertyDoesNotMaterialize() {

        def props = ['href': 'http://foo.com/test/123']
        InternalDataStore ds = createStrictMock(InternalDataStore)

        replay ds

        TestResource resource = new TestResource(ds, props)

        resource.setName('New Value')

        assertEquals 'New Value', resource.getName()

        verify ds
    }

    @Test
    void testDirtyPropertiesRetainedAfterMaterialization() {

        def props = ['href': 'http://foo.com/test/123']
        InternalDataStore ds = createStrictMock(InternalDataStore)

        def serverResource = new TestResource(ds, [
                'href': props.href,
                'name': 'Old Name',
                'description': 'Old Description'
        ])

        expect(ds.getResource(props.href, TestResource)).andReturn serverResource

        replay ds

        TestResource resource = new TestResource(ds, props)
        resource.setName('New Name')

        //get the description, which hasn't been set locally - this will force materialization:
        String description = resource.getDescription()

        assertEquals 'Old Description', description //obtained during materialization
        assertEquals 'New Name', resource.getName() //dirty property retained even after materialization

        verify ds
    }

}
