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
package com.stormpath.sdk.impl.saml

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.cert.X509SigningCert
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * Test for X509SigningCert class
 *
 * @since 1.0.RC8
 */
class DefaultX509SigningCertTest {

    @Test
    void testGetPropertyDescriptors() {

        X509SigningCert x509SigningCert = new DefaultX509SigningCert(createStrictMock(InternalDataStore))

        def propertyDescriptors = x509SigningCert.getPropertyDescriptors()
        assertEquals(propertyDescriptors.size(), 0)
    }

    @Test
    void testMethods() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/x509certificates/45YM3OwioW9PVtfLOh6q1e"]
        X509SigningCert mappingRules = new DefaultX509SigningCert(internalDataStore, properties)

        replay internalDataStore

        assertTrue(mappingRules.getHref().equals(properties.href))

        verify internalDataStore
    }
}
