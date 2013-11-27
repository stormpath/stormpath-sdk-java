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
package com.stormpath.sdk.impl.tenant

import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals

/**
 * @since 0.8
 */
class DefaultTenantTest {

    @Test
    void testCreateDirectory() {

        def properties = [ href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE",
                applications: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/applications"],
                directories: [href: "https://api.stormpath.com/v1/tenants/jaef0wq38ruojoiadE/directories"]]

        def internalDataStore = createStrictMock(InternalDataStore)

        def directory = createStrictMock(Directory)
        def returnedDirectory = createStrictMock(Directory)

        def defaultTenant = new DefaultTenant(internalDataStore, properties)

        expect(internalDataStore.create("/directories", directory)).andReturn(returnedDirectory)

        replay internalDataStore, directory, returnedDirectory

        assertEquals(defaultTenant.createDirectory(directory), returnedDirectory)

        verify internalDataStore, directory, returnedDirectory
    }



}