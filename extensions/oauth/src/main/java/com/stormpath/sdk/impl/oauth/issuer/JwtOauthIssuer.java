/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.oauth.issuer;

import com.stormpath.sdk.impl.ds.JacksonMapMarshaller;
import com.stormpath.sdk.impl.ds.MapMarshaller;
import com.stormpath.sdk.impl.oauth.issuer.signer.JwtSigner;
import com.stormpath.sdk.lang.Assert;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import java.util.Map;

/**
 * This is stateful instance.
 *
 * @since 1.0.RC
 */
public class JwtOauthIssuer implements OAuthIssuer {

    private final MapMarshaller mapMarshaller;

    private final Map<String, Object> payload;

    private final JwtSigner jwtSigner;

    public JwtOauthIssuer(JwtSigner jwtSigner, Map<String, Object> payload) {
        Assert.notNull(jwtSigner, "jwtSigner cannot be null.");
        Assert.notEmpty(payload, "payload cannot be null or empty.");
        this.jwtSigner = jwtSigner;
        this.payload = payload;
        this.mapMarshaller = new JacksonMapMarshaller();
    }

    @Override
    public String accessToken() {

        String serializedPayload = serializePayload();

        return jwtSigner.sign(serializedPayload);
    }

    @Override
    public String authorizationCode() throws OAuthSystemException {
        throw new UnsupportedOperationException("authorizationCode() method hasn't been implemented.");
    }

    @Override
    public String refreshToken() throws OAuthSystemException {
        throw new UnsupportedOperationException("refreshToken() method hasn't been implemented.");
    }

    private String serializePayload() {
        return mapMarshaller.marshal(payload);
    }
}
