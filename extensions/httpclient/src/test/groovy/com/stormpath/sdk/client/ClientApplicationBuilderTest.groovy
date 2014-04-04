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
    void testSetApiKeyProperties() {
        ClientBuilder clientBuilder = createStrictMock(ClientBuilder)
        ApiKeyBuilder apiKeyBuilder = createStrictMock(ApiKeyBuilder)
        def arg = createStrictMock(Properties);
        builder = new ClientApplicationBuilder(clientBuilder, apiKeyBuilder);

        expect(apiKeyBuilder.setProperties(same(arg))).andReturn apiKeyBuilder

        replay clientBuilder, arg, apiKeyBuilder

        builder.setApiKeyProperties(arg)

        verify clientBuilder, arg, apiKeyBuilder
    }

    @Test
    void testSetApiKeyReader() {
        ClientBuilder clientBuilder = createStrictMock(ClientBuilder)
        ApiKeyBuilder apiKeyBuilder = createStrictMock(ApiKeyBuilder)
        def arg = createStrictMock(Reader)
        builder = new ClientApplicationBuilder(clientBuilder, apiKeyBuilder);

        expect(apiKeyBuilder.setReader(same(arg))).andReturn apiKeyBuilder

        replay clientBuilder, arg, apiKeyBuilder

        builder.setApiKeyReader arg

        verify clientBuilder, arg, apiKeyBuilder
    }

    @Test
    void testSetApiKeyInputStream() {
        ClientBuilder clientBuilder = createStrictMock(ClientBuilder)
        ApiKeyBuilder apiKeyBuilder = createStrictMock(ApiKeyBuilder)
        def arg = createStrictMock(InputStream)
        builder = new ClientApplicationBuilder(clientBuilder, apiKeyBuilder);

        expect(apiKeyBuilder.setInputStream(same(arg))).andReturn apiKeyBuilder

        replay clientBuilder, arg, apiKeyBuilder

        builder.setApiKeyInputStream arg

        verify clientBuilder, arg, apiKeyBuilder
    }

    @Test
    void testSetApiKeyFileLocation() {
        ClientBuilder clientBuilder = createStrictMock(ClientBuilder)
        ApiKeyBuilder apiKeyBuilder = createStrictMock(ApiKeyBuilder)
        def arg = 'test'
        builder = new ClientApplicationBuilder(clientBuilder, apiKeyBuilder);

        expect(apiKeyBuilder.setFileLocation(eq(arg))).andReturn apiKeyBuilder

        replay clientBuilder, apiKeyBuilder

        builder.setApiKeyFileLocation arg

        verify clientBuilder, apiKeyBuilder
    }

    @Test
    void testSetApiKeyIdPropertyName() {
        ClientBuilder clientBuilder = createStrictMock(ClientBuilder)
        ApiKeyBuilder apiKeyBuilder = createStrictMock(ApiKeyBuilder)
        def arg = 'test'
        builder = new ClientApplicationBuilder(clientBuilder, apiKeyBuilder);

        expect(apiKeyBuilder.setIdPropertyName(eq(arg))).andReturn apiKeyBuilder

        replay clientBuilder, apiKeyBuilder

        builder.setApiKeyIdPropertyName arg

        verify clientBuilder, apiKeyBuilder
    }

    @Test
    void testSetApiKeySecretPropertyName() {
        ClientBuilder clientBuilder = createStrictMock(ClientBuilder)
        ApiKeyBuilder apiKeyBuilder = createStrictMock(ApiKeyBuilder)
        def arg = 'test'
        builder = new ClientApplicationBuilder(clientBuilder, apiKeyBuilder);

        expect(apiKeyBuilder.setSecretPropertyName(eq(arg))).andReturn apiKeyBuilder

        replay clientBuilder, apiKeyBuilder

        builder.setApiKeySecretPropertyName arg

        verify clientBuilder, apiKeyBuilder
    }

    @Test
    void testBuildClient() {
        Client client = createStrictMock(Client)
        ApiKey apiKey = createStrictMock(ApiKey)

        ClientBuilder clientBuilder = createStrictMock(ClientBuilder)
        ApiKeyBuilder apiKeyBuilder = createStrictMock(ApiKeyBuilder)
        builder = new ClientApplicationBuilder(clientBuilder, apiKeyBuilder);

        expect(apiKeyBuilder.build()).andReturn apiKey
        expect(clientBuilder.setApiKey(apiKey)).andReturn clientBuilder
        expect(clientBuilder.build()).andReturn client

        replay clientBuilder, client, apiKeyBuilder, apiKey

        assertSame builder.buildClient(), client

        verify clientBuilder, client, apiKeyBuilder, apiKey
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
        builder.build()
    }

    @Test
    void testBuildApplicationWithUserInfoUrl() {

        final Client client = createStrictMock(Client)
        DataStore ds = createStrictMock(DataStore)
        Application application = createStrictMock(Application)

        expect(client.getDataStore()).andStubReturn ds
        expect(ds.getResource(eq('https://api.stormpath.com/v1/applications/appUid'), same(Application))).andReturn application

        replay client, ds, application

        String id = null;
        String secret = null

        builder = new ClientApplicationBuilder() {

            @Override
            protected Client buildClient() {
                return client;
            }

            @Override
            ClientApplicationBuilder setApiKeyProperties(Properties properties) {
                id = properties.get('apiKey.id')
                secret = properties.get('apiKey.secret')
                return super.setApiKeyProperties(properties)
            }
        }

        String href = 'https://foo:bar@api.stormpath.com/v1/applications/appUid'

        builder.applicationHref = href
        def clientApp = builder.build();

        assertNotNull clientApp
        assertSame clientApp.client, client
        assertSame clientApp.application, application
        assertEquals 'foo', id
        assertEquals 'bar', secret

        verify client, ds, application
    }
}
