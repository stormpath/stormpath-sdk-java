/*
 * Copyright 2017 Stormpath, Inc.
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
package com.stormpath.spring.cloud.zuul.autoconfigure

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.Client
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.TextCodec
import java.security.SecureRandom
import org.testng.annotations.Test
import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.3.0
 */
class ClientApplicationJwkFactoryTest {

    private static final Random RANDOM = new SecureRandom()

    @Test(expectedExceptions = [IllegalArgumentException])
    void testNullClient() {
        new ClientApplicationJwkFactory(null, createMock(Application))
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testNullApplication() {
        new ClientApplicationJwkFactory(createMock(Client), null)
    }

    @Test
    void testNullSigAlg32ByteKey() {
        testSigAlgWithNByteKey(32, null, SignatureAlgorithm.HS256)
    }

    @Test
    void testNullSigAlg48ByteKey() {
        testSigAlgWithNByteKey(48, null, SignatureAlgorithm.HS384)
    }

    @Test
    void testNullSigAlg64ByteKey() {
        testSigAlgWithNByteKey(64, null, SignatureAlgorithm.HS512)
    }

    @Test
    void testRsaSigAlg32ByteKey() {
        testSigAlgWithNByteKey(32, SignatureAlgorithm.RS256, SignatureAlgorithm.HS256)
    }

    @Test
    void testRsaSigAlg48ByteKey() {
        testSigAlgWithNByteKey(48, SignatureAlgorithm.RS384, SignatureAlgorithm.HS384)
    }

    @Test
    void testRsaSigAlg64ByteKey() {
        testSigAlgWithNByteKey(64, SignatureAlgorithm.RS512, SignatureAlgorithm.HS512)
    }

    void testSigAlgWithNByteKey(int numBytes, SignatureAlgorithm specified, SignatureAlgorithm expected) {

        def app = createMock(Application)
        def client = createMock(Client)
        def apiKey = createMock(ApiKey)

        def apiBaseUrl = 'https://api.stormpath.com/v1'
        def apiKeyId = 'myApiKeyId'
        byte[] secret = new byte[numBytes];
        RANDOM.nextBytes(secret)
        expect(apiKey.getSecret()).andStubReturn(TextCodec.BASE64.encode(secret))
        expect(client.getApiKey()).andReturn(apiKey)
        expect(apiKey.getId()).andStubReturn(apiKeyId)
        expect(app.getHref()).andStubReturn(apiBaseUrl + "/applications/myAppId");

        replay app, client, apiKey

        def result = new ClientApplicationJwkFactory(client, app).apply(specified)

        assertNotNull result
        assertEquals result.keyId, apiBaseUrl + '/apiKeys/' + apiKeyId
        assertEquals result.getKey().getEncoded(), secret
        assertEquals result.signatureAlgorithm, expected

        verify app, client, apiKey
    }
}
