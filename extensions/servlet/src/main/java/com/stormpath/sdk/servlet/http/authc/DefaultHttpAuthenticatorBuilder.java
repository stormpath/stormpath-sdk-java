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
package com.stormpath.sdk.servlet.http.authc;

import com.stormpath.sdk.lang.Assert;

import java.util.ArrayList;
import java.util.List;

public class DefaultHttpAuthenticatorBuilder implements HttpAuthenticatorBuilder {

    private final List<HttpAuthenticationScheme> schemes;
    private boolean sendChallenge = true;

    public DefaultHttpAuthenticatorBuilder() {
        this.schemes = new ArrayList<HttpAuthenticationScheme>();
    }

    @Override
    public HttpAuthenticatorBuilder addScheme(HttpAuthenticationScheme scheme) {
        Assert.notNull(scheme, "scheme cannot be null.");
        this.schemes.add(scheme);
        return this;
    }

    @Override
    public HttpAuthenticatorBuilder addSchemes(List<HttpAuthenticationScheme> schemes) {
        Assert.notEmpty(schemes, "schemes cannot be null or empty.");
        this.schemes.addAll(schemes);
        return this;
    }

    @Override
    public HttpAuthenticatorBuilder sendChallenge(boolean sendChallenge) {
        this.sendChallenge = sendChallenge;
        return this;
    }

    @Override
    public HttpAuthenticator build() {
        Assert.notEmpty(this.schemes, "No authentication schemes specified.");
        return new AuthorizationHeaderAuthenticator(this.schemes, sendChallenge);
    }
}
