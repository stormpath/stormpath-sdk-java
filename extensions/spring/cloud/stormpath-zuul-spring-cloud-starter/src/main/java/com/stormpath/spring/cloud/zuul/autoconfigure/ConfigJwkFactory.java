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
package com.stormpath.spring.cloud.zuul.autoconfigure;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Function;
import com.stormpath.sdk.lang.RuntimeEnvironment;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.spring.cloud.zuul.config.DefaultJwkResult;
import com.stormpath.spring.cloud.zuul.config.JwkConfig;
import com.stormpath.spring.cloud.zuul.config.JwkResult;
import com.stormpath.spring.cloud.zuul.config.PemResourceKeyResolver;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PrivateKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;

/**
 * @since 1.3.0
 */
public class ConfigJwkFactory implements Function<JwkConfig, JwkResult> {

    private final RuntimeEnvironment runtimeEnvironment;
    private final Function<SignatureAlgorithm, JwkResult> defaultKeyFunction;

    public ConfigJwkFactory(RuntimeEnvironment runtimeEnvironment,
                            Function<SignatureAlgorithm, JwkResult> defaultKeyFunction) {
        Assert.notNull(runtimeEnvironment);
        Assert.notNull(defaultKeyFunction);
        this.runtimeEnvironment = runtimeEnvironment;
        this.defaultKeyFunction = defaultKeyFunction;
    }

    @Override
    public JwkResult apply(JwkConfig jwk) {

        SignatureAlgorithm signatureAlgorithm = null;
        String kid = jwk.getId();
        Key key = null;

        String value = jwk.getAlg();
        if (value != null) {
            try {
                signatureAlgorithm = SignatureAlgorithm.forName(value);
            } catch (SignatureException e) {
                String msg = "Unsupported stormpath.zuul.account.header.jwt.key.alg value: " + value + ".  " +
                    "Please use only " + SignatureAlgorithm.class.getName() + " enum names: " +
                    Strings.arrayToCommaDelimitedString(SignatureAlgorithm.values()).replace("NONE,", "");
                throw new IllegalArgumentException(msg, e);
            }
        }

        byte[] bytes = null;

        Resource keyResource = jwk.getResource();

        String keyString = jwk.getValue();

        boolean keyStringSpecified = Strings.hasText(keyString);

        if (keyResource != null && keyStringSpecified) {
            String msg = "Both the stormpath.zuul.account.header.jwt.key.value and " +
                "stormpath.zuul.account.header.jwt.key.resource properties may not be set simultaneously.  " +
                "Please choose one.";
            throw new IllegalArgumentException(msg);
        }

        if (keyStringSpecified) {

            String encoding = jwk.getEncoding();

            if (keyString.startsWith(PemResourceKeyResolver.PEM_PREFIX)) {
                encoding = "pem";
            }

            if (encoding == null) {
                //default to the JWK specification format:
                encoding = "base64url";
            }

            if (encoding.equalsIgnoreCase("base64url")) {
                bytes = TextCodec.BASE64URL.decode(keyString);
            } else if (encoding.equalsIgnoreCase("base64")) {
                bytes = TextCodec.BASE64.decode(keyString);
            } else if (encoding.equalsIgnoreCase("utf8")) {
                bytes = keyString.getBytes(StandardCharsets.UTF_8);
            } else if (encoding.equalsIgnoreCase("pem")) {
                byte[] resourceBytes = keyString.getBytes(StandardCharsets.UTF_8);
                ByteArrayInputStream bais = new ByteArrayInputStream(resourceBytes);
                String description = "stormpath.zuul.account.header.jwt.key.value";
                keyResource = new InputStreamResource(bais, description);
            } else {
                throw new IllegalArgumentException("Unsupported encoding '" + encoding + "'.  Supported " +
                    "encodings: base64url, base64, utf8, pem.");
            }
        }

        if (bytes != null && bytes.length > 0) { //symmetric key

            if (signatureAlgorithm == null) {
                //choose the best available alg based on available key:
                signatureAlgorithm = getAlgorithm(bytes);
            }

            if (!signatureAlgorithm.isHmac()) {
                String algName = signatureAlgorithm.name();
                String msg = "It appears that the stormpath.zuul.account.header.jwt.key.value " +
                    "is a shared (symmetric) secret key, and this requires the " +
                    "stormpath.zuul.account.header.jwt.key.alg value to equal HS256, HS384, or HS512. " +
                    "The specified stormpath.zuul.account.header.jwt.key.alg value is " + algName + ". " +
                    "If you wish to use the " + algName + " algorithm, please ensure that either 1) " +
                    "stormpath.zuul.account.header.jwt.key.value is a private asymmetric PEM-encoded string, " +
                    "or 2) set the stormpath.zuul.account.header.jwt.key.resource property to a Spring " +
                    "Resource path where the PEM-encoded key file resides, or " +
                    "or 3) define a bean named 'stormpathForwardedAccountJwtSigningKey' that returns an " +
                    signatureAlgorithm.getFamilyName() + " private key instance.";
                throw new IllegalArgumentException(msg);
            }

            key = new SecretKeySpec(bytes, signatureAlgorithm.getJcaName());
        }

        if (keyResource != null) {

            Function<Resource,Key> resourceKeyResolver = createResourceKeyFunction(keyResource, keyStringSpecified);
            Assert.notNull(resourceKeyResolver, "resourceKeyResolver instance cannot be null.");
            key = resourceKeyResolver.apply(keyResource);
            if (key == null) {
                String msg = "Resource to Key resolver/function did not return a key for specified resource [" +
                    keyResource + "].  If providing your own implementation of this function, ensure it does not " +
                    "return null.";
                throw new IllegalStateException(msg);
            }

            Assert.notNull(key, "ResourceKeyResolver function did not return a key for specified resource [" + keyResource + "]");

            if (signatureAlgorithm == null) {
                if (key instanceof RSAKey) {
                    signatureAlgorithm = SignatureAlgorithm.RS256;
                } else if (key instanceof ECKey) {
                    signatureAlgorithm = SignatureAlgorithm.ES256;
                } else {
                    String msg = "Unable to detect jwt signing key type to provide a default signature " +
                        "algorithm.  Please specify the stormpath.zuul.account.header.jwt.key.alg property.";
                    throw new IllegalArgumentException(msg);
                }
            }

            if (key instanceof RSAKey && !signatureAlgorithm.getFamilyName().equalsIgnoreCase("RSA")) {
                String msg = "Signature algorithm [" + signatureAlgorithm + "] is not " +
                    "compatible with the specified RSA key.";
                throw new IllegalArgumentException(msg);
            }

            if (key instanceof ECKey && !signatureAlgorithm.getFamilyName().equalsIgnoreCase("Elliptic Curve")) {
                String msg = "Signature algorithm [" + signatureAlgorithm + "] is not " +
                    "compatible with the specified Elliptic Curve key.";
                throw new IllegalArgumentException(msg);
            }

            Assert.isTrue(key instanceof PrivateKey, "Specified asymmetric signing key is not a PrivateKey.  " +
                "Please ensure you specify a private (not public) key.");
        }

        if (key == null) {
            //a key was not provided as a bean, nor was one configured via app config properties.
            //fall back to the default key as the signing key
            return defaultKeyFunction.apply(signatureAlgorithm);
        }

        return new DefaultJwkResult(signatureAlgorithm, key, kid);
    }

    protected Function<Resource,Key> createResourceKeyFunction(Resource keyResource, boolean keyStringSpecified) {

        if (!runtimeEnvironment.isClassAvailable("org.bouncycastle.openssl.PEMParser")) {

            String msg = "The org.bouncycastle:bcpkix-jdk15on:1.56 artifact (or newer) must be in the " +
                "classpath to be able to parse the " +
                (keyStringSpecified ?
                    "stormpath.zuul.account.header.jwt.key.value PEM-encoded value" :
                    "stormpath.zuul.account.header.jwt.key.resource [" + keyResource + "].");
            throw new IllegalStateException(msg);
        }

        return new PemResourceKeyResolver(true); //origin server == private key
    }

    //package private on purpose
    static SignatureAlgorithm getAlgorithm(byte[] hmacSigningKeyBytes) {
        Assert.isTrue(hmacSigningKeyBytes != null && hmacSigningKeyBytes.length > 0,
            "hmacSigningBytes cannot be null or empty.");
        if (hmacSigningKeyBytes.length >= 64) {
            return SignatureAlgorithm.HS512;
        } else if (hmacSigningKeyBytes.length >= 48) {
            return SignatureAlgorithm.HS384;
        } else { //<= 32
            return SignatureAlgorithm.HS256;
        }
    }
}
