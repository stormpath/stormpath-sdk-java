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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.JwtValidationRequest;
import com.stormpath.sdk.oauth.JwtValidationRequestBuilder;

/**
 * @since 1.0.RC6
 */
public class DefaultJwtValidationRequestBuilder implements JwtValidationRequestBuilder {

    String jwt;
    boolean withLocalValidation = false;

    public DefaultJwtValidationRequestBuilder() {
    }

    @Override
    public JwtValidationRequestBuilder setJwt(String jwt) {
        Assert.notNull(jwt, "jwt is mandatory and cannot be empty.");
        this.jwt = jwt;
        return this;
    }

    public JwtValidationRequest build() {
        Assert.notNull(jwt, "jwt is mandatory and cannot be empty.");

        JwtValidationRequest request = new DefaultJwtValidationRequest(jwt, withLocalValidation);
        return request;
    }

    @Override
    public JwtValidationRequestBuilder withLocalValidation() {
        this.withLocalValidation = true;
        return this;
    }
}
