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
package com.stormpath.sdk.impl.oauth.authc;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.oauth.issuer.HmacValueGenerator;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationResult;
import org.apache.oltu.oauth2.as.issuer.ValueGenerator;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;

import java.util.TimeZone;

/**
 * @since 1.0.RC
 */
public class OAuthBearerAuthenticator {

    public final static char SCOPE_SEPARATOR = ':';

    public static long TOKEN_DURATION = 3600l;

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private final ValueGenerator valueGenerator;

    private final Application application;

    public OAuthBearerAuthenticator(Application application, String apiKeySecret) {
        Assert.notNull(application);
        Assert.hasText(apiKeySecret);

        this.application = application;
        this.valueGenerator = new HmacValueGenerator(apiKeySecret);
    }

    public BasicOauthAuthenticationResult authenticate(Application application, DefaultBearerOauthAuthenticationRequest request) {

        OAuthAccessResourceRequest oAuthAccessResourceRequest;

        return null;

    }
}
