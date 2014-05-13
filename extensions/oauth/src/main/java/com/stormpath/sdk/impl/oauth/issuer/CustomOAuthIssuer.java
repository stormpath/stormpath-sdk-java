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

import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.ValueGenerator;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

/**
 * CustomOAuthIssuer
 *
 * @since 1.0.RC
 */
public class CustomOAuthIssuer implements OAuthIssuer {

    private final String tokenData;
    private final ValueGenerator valueGenerator;

    public CustomOAuthIssuer(ValueGenerator valueGenerator, String tokenData) {
        this.valueGenerator = valueGenerator;
        this.tokenData = tokenData;
    }

    @Override
    public String accessToken() throws OAuthSystemException {
        return valueGenerator.generateValue(tokenData);
    }

    @Override
    public String authorizationCode() throws OAuthSystemException {
        return valueGenerator.generateValue(tokenData);
    }

    @Override
    public String refreshToken() throws OAuthSystemException {
        return valueGenerator.generateValue(tokenData);
    }
}
