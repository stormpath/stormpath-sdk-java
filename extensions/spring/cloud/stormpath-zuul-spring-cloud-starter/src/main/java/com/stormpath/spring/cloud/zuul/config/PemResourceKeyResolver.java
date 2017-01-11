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
package com.stormpath.spring.cloud.zuul.config;

import com.stormpath.sdk.lang.Function;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Key;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @since 1.3.0
 */
public class PemResourceKeyResolver implements Function<Resource, Key> {

    public static final String PEM_PREFIX = "-----BEGIN ";

    private final JcaX509CertificateConverter x509Converter = new JcaX509CertificateConverter().setProvider("BC");

    private final JcaPEMKeyConverter pemKeyConverter = new JcaPEMKeyConverter().setProvider("BC");

    private final boolean findPrivate;

    public PemResourceKeyResolver(boolean findPrivate) {
        this.findPrivate = findPrivate;
    }

    @Override
    public Key apply(Resource resource) {
        try {
            return doApply(resource);
        } catch (IOException | CertificateException e) {
            String msg = "Unable to parse resource [" + resource + "]: " + e.getMessage();
            throw new IllegalArgumentException(msg, e);
        }
    }

    private Key doApply(Resource resource) throws IOException, CertificateException {

        try (Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"))) {

            PEMParser pemParser = new PEMParser(reader);

            Object o;

            boolean encryptedPrivateFound = false;

            while ((o = pemParser.readObject()) != null) {

                if (o instanceof PKCS8EncryptedPrivateKeyInfo) {
                    encryptedPrivateFound = true;
                }

                if (o instanceof PEMKeyPair) {
                    PEMKeyPair pemKeyPair = (PEMKeyPair) o;
                    return findPrivate ?
                        pemKeyConverter.getPrivateKey(pemKeyPair.getPrivateKeyInfo()) :
                        pemKeyConverter.getPublicKey(pemKeyPair.getPublicKeyInfo());
                }

                if (o instanceof PrivateKeyInfo && findPrivate) {
                    PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) o;
                    return pemKeyConverter.getPrivateKey(privateKeyInfo);
                }

                if (o instanceof SubjectPublicKeyInfo && !findPrivate) {
                    SubjectPublicKeyInfo info = (SubjectPublicKeyInfo) o;
                    return pemKeyConverter.getPublicKey(info);
                }

                if (o instanceof X509CertificateHolder && !findPrivate) {
                    X509CertificateHolder holder = (X509CertificateHolder) o;
                    X509Certificate cert = x509Converter.getCertificate(holder);
                    return cert.getPublicKey();
                }
            }

            //if we haven't returned yet, we couldn't find a key based on our preferences.

            String msg;

            if (encryptedPrivateFound && findPrivate) {
                msg = "Key resource [" + resource + "] contains a PKCS8 Encrypted PrivateKey.  Only unencrypted " +
                    "private keys are supported.";
            } else {
                msg = "Key resource [" + resource + "] did not contain a " + (findPrivate ? "private " : "public ") +
                    "key.";
            }

            throw new IllegalArgumentException(msg);
        }
    }
}
