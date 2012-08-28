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
package com.stormpath.sdk.client

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.ds.DataStore
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.5
 */
class ClientApplicationBuilderTest {

    ClientApplicationBuilder builder

    @BeforeTest
    void setUp() {
        builder = new ClientApplicationBuilder();
    }

    @Test
    void testGetHrefWithUserInfo() {

        String href = 'https://foo:bar@api.stormpath.com/v1/applications/appUid'

        int i = href.indexOf((int) ('@' as char))

        String[] parts = builder.getHrefWithUserInfo(href, i)

        assertEquals parts[0], 'https://'
        assertEquals parts[1], 'foo:bar'
        assertEquals parts[2], 'api.stormpath.com/v1/applications/appUid'
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testGetHrefWithUserInfoWithInvalidUrl() {
        builder.getHrefWithUserInfo('whatever', 1)
    }

    @Test
    void testCreateApiKeyProperties() {

        String[] pair = ['foo', 'bar'];

        Properties props = builder.createApiKeyProperties(pair);

        assertEquals props.get('apiKey.id'), 'foo'
        assertEquals props.get('apiKey.secret'), 'bar'
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testCreateApiPropertiesWithNullPair() {
        builder.createApiKeyProperties(null)
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testCreateApiPropertiesWithInvalidLengthArgs() {
        String[] args = ['foo']
        builder.createApiKeyProperties(args)
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testUrlDecode() {
        builder = new ClientApplicationBuilder() {
            @Override
            protected String urlDecode(String s, String encoding) throws UnsupportedEncodingException {
                throw new UnsupportedEncodingException("Test");
            }
        }
        builder.urlDecode("foo")
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testBuildApplicationWithNullHref() {
        builder.buildApplication()
    }

    @Test
    void testBuildApplicationWithUserInfoUrl() {

        final Client client = createStrictMock(Client)
        DataStore ds = createStrictMock(DataStore)
        Application application = createStrictMock(Application)

        expect(client.getDataStore()).andStubReturn ds
        expect(ds.getResource(eq('https://api.stormpath.com/v1/applications/appUid'), same(Application))).andReturn application

        replay client, ds, application

        builder = new ClientApplicationBuilder() {
            @Override
            protected Client createClient(ApiKey key, String baseUrl) {
                assertEquals key.id, 'foo'
                assertEquals key.secret, 'bar'
                return client;
            }
        }

        String href = 'https://foo:bar@api.stormpath.com/v1/applications/appUid'

        builder.applicationHref = href
        def clientApp = builder.buildApplication();

        assertNotNull clientApp
        assertSame clientApp.client, client
        assertSame clientApp.application, application

        verify client, ds, application
    }
}
