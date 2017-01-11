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

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Function;
import com.stormpath.spring.cloud.zuul.config.DefaultJwkResult;
import com.stormpath.spring.cloud.zuul.config.JwkResult;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

import static com.stormpath.spring.cloud.zuul.autoconfigure.ConfigJwkFactory.*;

/**
 * @since 1.3.0
 */
public class ClientApplicationJwkFactory implements Function<SignatureAlgorithm, JwkResult> {

    private static final Logger log = LoggerFactory.getLogger(ClientApplicationJwkFactory.class);

    private final Client client;

    private final Application application;

    public ClientApplicationJwkFactory(Client client, Application application) {
        Assert.notNull(client);
        Assert.notNull(application);
        this.client = client;
        this.application = application;
    }

    @Override
    public JwkResult apply(SignatureAlgorithm signatureAlgorithm) {

        //fall back to client api key secret:
        ApiKey apiKey = client.getApiKey();

        //Set a 'kid' equal to the api key href:
        String href = application.getHref();
        int i = href.indexOf("/applications/");
        href = href.substring(0, i);
        href += "/apiKeys/" + apiKey.getId();
        String kid = href;

        String secret = apiKey.getSecret();
        byte[] bytes = TextCodec.BASE64.decode(secret);

        SignatureAlgorithm defaultSigAlg = getAlgorithm(bytes);
        if (signatureAlgorithm == null) {
            signatureAlgorithm = defaultSigAlg;
        }

        if (!signatureAlgorithm.isHmac()) {
            String msg = "Unable to use specified JWT signature algorithm '" + signatureAlgorithm + "' when " +
                "creating X-Forwarded-User JWTs, as this algorithm is incompatible with the " +
                "fallback/default Stormpath Client ApiKey secret signing key.  Defaulting to '" +
                defaultSigAlg + "'.  To avoid this message, either 1) do not specify a signature algorithm to " +
                "let the framework choose an algorithm appropriate for the default signing key, or 2) define " +
                "a 'stormpathForwardedAccountJwtSigningKey' bean of type java.security.Key that is " +
                "compatible with your specified signature algorithm.";
            log.warn(msg);
            signatureAlgorithm = defaultSigAlg;
        }

        Key key = new SecretKeySpec(bytes, signatureAlgorithm.getJcaName());

        return new DefaultJwkResult(signatureAlgorithm, key, kid);
    }
}
