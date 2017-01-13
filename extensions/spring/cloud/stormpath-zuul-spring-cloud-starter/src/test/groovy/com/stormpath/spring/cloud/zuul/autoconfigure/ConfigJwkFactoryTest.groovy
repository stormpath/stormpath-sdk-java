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

import com.stormpath.sdk.lang.DefaultRuntimeEnvironment
import com.stormpath.sdk.lang.Function
import com.stormpath.sdk.lang.RuntimeEnvironment
import com.stormpath.spring.cloud.zuul.config.DefaultJwkResult
import com.stormpath.spring.cloud.zuul.config.JwkConfig
import com.stormpath.spring.cloud.zuul.config.JwkResult
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.TextCodec
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.testng.annotations.Test

import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.interfaces.ECKey
import java.security.interfaces.RSAKey

import static org.testng.Assert.*

import static io.jsonwebtoken.SignatureAlgorithm.*

/**
 * @since 1.3.0
 */
class ConfigJwkFactoryTest {

    private static final RuntimeEnvironment RUNENV = DefaultRuntimeEnvironment.INSTANCE;
    private static final Random RANDOM = new SecureRandom()

    private static Function<SignatureAlgorithm,JwkResult> defaultKeyFn() {
        return new Function<SignatureAlgorithm, JwkResult>() {
            @Override
            JwkResult apply(SignatureAlgorithm signatureAlgorithm) {
                byte[] secret = new byte[32]
                RANDOM.nextBytes(secret)
                def alg = HS256
                return new DefaultJwkResult(alg, new SecretKeySpec(secret, alg.jcaName), 'foo')
            }
        }
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testNullRuntimeEnvironment() {
        new ConfigJwkFactory(null, defaultKeyFn())
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testNullApplication() {
        new ConfigJwkFactory(RUNENV, null)
    }

    @Test
    void testDefaultKeyFunction() {
        def jwk = new JwkConfig()
        def result = new ConfigJwkFactory(RUNENV, defaultKeyFn()).apply(jwk)
        assertNotNull result
        assertEquals result.keyId, 'foo'
        assertEquals result.getSignatureAlgorithm(), HS256
        assertTrue result.key.getEncoded().length == 32
    }

    @Test
    void testHS256KeyString() {
        testSymmetricKey(new JwkConfig(value: newKeyString(32)), HS256)
    }

    @Test
    void testHS384KeyString() {
        testSymmetricKey(new JwkConfig(value: newKeyString(48)), HS384)
    }

    @Test
    void testHS512KeyString() {
        testSymmetricKey(new JwkConfig(value: newKeyString(64)), HS512)
    }

    @Test
    void testKeyIdPreserved() {
        testSymmetricKey(new JwkConfig(value: newKeyString(32), id: 'foo'), HS256)
    }

    @Test
    void testHS256WithAlg() {
        testSymmetricKey(new JwkConfig(value: newKeyString(32), id: 'foo', alg: 'HS256'), HS256)
    }

    @Test
    void testHS384WithAlg() {
        testSymmetricKey(new JwkConfig(value: newKeyString(32), id: 'foo', alg: 'HS384'), HS384)
    }

    @Test
    void testHS512WithAlg() {
        testSymmetricKey(new JwkConfig(value: newKeyString(32), id: 'foo', alg: 'HS512'), HS512)
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testHS256WithUnknownAlg() {
        testSymmetricKey(new JwkConfig(value: newKeyString(32), id: 'foo', alg: 'unknown'), HS256)
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testBothKeyValueAndResourcePathSpecified() {
        def dummyResource = new InputStreamResource(new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8)))
        testSymmetricKey(new JwkConfig(value: newKeyString(32), resource: dummyResource), HS256)
    }

    @Test
    void testUnsupportedKeyStringEncoding() {
        byte[] keyBytes = newKeyBytes(32)
        String keyString = TextCodec.BASE64.encode(keyBytes)
        def enc = 'whatever'
        def jwk = new JwkConfig(value: keyString, encoding: "whatever")
        try {
            new ConfigJwkFactory(RUNENV, defaultKeyFn()).apply(jwk)
            fail("Exception expected.")
        } catch (IllegalArgumentException iae) {
            assertEquals iae.getMessage(), "Unsupported encoding '$enc'.  Supported encodings: base64url, base64, utf8, pem."
        }
    }

    @Test
    void testBase64KeyString() {
        byte[] keyBytes = newKeyBytes(32)
        String keyString = TextCodec.BASE64.encode(keyBytes)
        def jwk = new JwkConfig(value: keyString, encoding: "base64")
        def result = new ConfigJwkFactory(RUNENV, defaultKeyFn()).apply(jwk)
        assertEquals result.keyId, jwk.id
        assertEquals result.key.encoded, TextCodec.BASE64.decode(jwk.value)
        assertEquals result.signatureAlgorithm, HS256
    }

    @Test
    void testSymmetricKeyWithoutHmacAlg() {
        byte[] keyBytes = newKeyBytes(32)
        String keyString = TextCodec.BASE64URL.encode(keyBytes)
        def jwk = new JwkConfig(value: keyString, alg: RS256)
        try {
            new ConfigJwkFactory(RUNENV, defaultKeyFn()).apply(jwk)
        } catch (IllegalArgumentException iae) {
            assertTrue iae.getMessage().contains("stormpath.zuul.account.header.jwt.key.value") &&
                    iae.getMessage().contains("stormpath.zuul.account.header.jwt.key.alg")
        }
    }

    @Test
    void testUtf8KeyString() {
        byte[] keyBytes = newKeyBytes(32)
        String keyString = new String(keyBytes, StandardCharsets.UTF_8)
        def jwk = new JwkConfig(value: keyString, encoding: "utf8")
        def result = new ConfigJwkFactory(RUNENV, defaultKeyFn()).apply(jwk)
        assertEquals result.keyId, jwk.id
        assertEquals result.key.encoded, jwk.value.getBytes(StandardCharsets.UTF_8)
    }

    @Test
    void testPemKeyString() {
        def resource = new ClassPathResource("rsatest.priv.pem")
        def scanner = new Scanner(resource.getInputStream()).useDelimiter("\\A");
        String keyString = scanner.hasNext() ? scanner.next() : "";

        def jwk = new JwkConfig(value: keyString)
        def result = new ConfigJwkFactory(RUNENV, defaultKeyFn()).apply(jwk)
        assertEquals result.keyId, jwk.id
        assertNotNull result.key
        assertTrue result.key instanceof RSAKey && result.key instanceof PrivateKey
        assertEquals result.signatureAlgorithm, RS256
    }

    private byte[] newKeyBytes(int numBytes) {
        byte[] secret = new byte[numBytes]
        RANDOM.nextBytes(secret)
        return secret;
    }

    private String newKeyString(int numBytes) {
        byte[] secret = newKeyBytes(numBytes);
        return TextCodec.BASE64URL.encode(secret);
    }


    private JwkResult testSymmetricKey(JwkConfig jwk, SignatureAlgorithm expectedAlg) {
        def result = new ConfigJwkFactory(RUNENV, defaultKeyFn()).apply(jwk)
        assertEquals result.keyId, jwk.id
        assertEquals result.key.encoded, TextCodec.BASE64URL.decode(jwk.value)
        assertEquals result.signatureAlgorithm, expectedAlg
        return result
    }

    @Test
    void testRsaPrivateKeyResource() {
        def jwk = new JwkConfig(resource: new ClassPathResource("rsatest.priv.pem"))
        def result = new ConfigJwkFactory(RUNENV, defaultKeyFn()).apply(jwk)
        assertEquals result.keyId, jwk.id
        assertTrue result.key instanceof RSAKey && result.key instanceof PrivateKey
        assertEquals result.signatureAlgorithm, RS256
    }

    @Test
    void testRsaPrivateKeyResourceWithInvalidAlg() {
        def alg = 'ES256'
        def jwk = new JwkConfig(resource: new ClassPathResource("rsatest.priv.pem"), alg: alg)
        try {
            new ConfigJwkFactory(RUNENV, defaultKeyFn()).apply(jwk)
            fail("Exception expected.")
        } catch (IllegalArgumentException iae) {
            assertEquals iae.getMessage(), "Signature algorithm [$alg] is not compatible with the specified RSA key."
        }
    }

    @Test
    void testRsaPublicKeyResource() { //public keys should never be used to sign something
        def path = "rsatest.pub.pem"
        def jwk = new JwkConfig(resource: new ClassPathResource(path))
        try {
            new ConfigJwkFactory(RUNENV, defaultKeyFn()).apply(jwk)
            fail("Exception expected.")
        } catch (IllegalArgumentException iae) {
            assertEquals iae.getMessage(), "Key resource [class path resource [$path]] did not contain a private key."
        }
    }

    @Test
    void testEllipticCurvePrivateKeyResource() {
        def jwk = new JwkConfig(resource: new ClassPathResource("secp384r1.priv.pem"))
        def result = new ConfigJwkFactory(RUNENV, defaultKeyFn()).apply(jwk)
        assertEquals result.keyId, jwk.id
        assertTrue result.key instanceof ECKey && result.key instanceof PrivateKey
        assertEquals result.signatureAlgorithm, ES256
    }

    @Test
    void testEllipticCurvePrivateKeyResourceWithInvalidAlg() {
        def alg = 'RS256'
        def jwk = new JwkConfig(resource: new ClassPathResource("secp384r1.priv.pem"), alg: alg)
        try {
            new ConfigJwkFactory(RUNENV, defaultKeyFn()).apply(jwk)
            fail("Exception expected.")
        } catch (IllegalArgumentException iae) {
            assertEquals iae.getMessage(), "Signature algorithm [$alg] is not compatible with the specified Elliptic Curve key."
        }
    }

    @Test(expectedExceptions = [IllegalStateException])
    void testResourceKeyResolverReturningNull() {
        def jwk = new JwkConfig(resource: new ClassPathResource("secp384r1.priv.pem"))
        def factory = new ConfigJwkFactory(RUNENV, defaultKeyFn()) {
            @Override
            protected Function<Resource, Key> createResourceKeyFunction(Resource keyResource, boolean keyStringSpecified) {
                return new Function<Resource, Key>() {
                    @Override
                    Key apply(Resource resource) {
                        return null;
                    }
                }
            }
        }
        factory.apply(jwk);
    }

    @Test
    void testResourceKeyResolverReturnsUnsupportedKey() {
        def jwk = new JwkConfig(resource: new ClassPathResource("secp384r1.priv.pem"))
        def factory = new ConfigJwkFactory(RUNENV, defaultKeyFn()) {
            @Override
            protected Function<Resource, Key> createResourceKeyFunction(Resource keyResource, boolean keyStringSpecified) {
                return new Function<Resource, Key>() {
                    @Override
                    Key apply(Resource resource) {
                        return new SecretKeySpec(newKeyBytes(32), HS256.jcaName)
                    }
                }
            }
        }
        try {
            factory.apply(jwk);
            fail("Exception expected.")
        } catch (IllegalArgumentException iae) {
            //ensure we're being helpful by telling them which property to configure to avoid this problem:
            assertEquals iae.message, "Unable to detect jwt signing key type to provide a default signature " +
            "algorithm.  Please specify the stormpath.zuul.account.header.jwt.key.alg property."
        }
    }

    @Test
    void testRsaPrivateKeyResourceWithBouncyCastleUnavailable() {
        def jwk = new JwkConfig(resource: new ClassPathResource("rsatest.priv.pem"))
        def runtimeEnvironment = new RuntimeEnvironment() {
            @Override
            boolean isClassAvailable(String fqcn) {
                return false //simulate a failed lookup
            }
        }
        try {
            new ConfigJwkFactory(runtimeEnvironment, defaultKeyFn()).apply(jwk)
            fail("Should have thrown an exception.")
        } catch (IllegalStateException iae) {
            //ensure we're helpful and tell the app developer which dependency to include:
            assertTrue(iae.getMessage().contains('org.bouncycastle:bcpkix-jdk15on:1.56'))
        }
    }

}
