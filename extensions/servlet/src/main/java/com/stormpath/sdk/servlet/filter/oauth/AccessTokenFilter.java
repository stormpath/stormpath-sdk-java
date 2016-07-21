/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.filter.oauth;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.BasicAuthenticationScheme;
import com.stormpath.sdk.servlet.mvc.AccessTokenController;

import javax.servlet.ServletException;

/**
 * @since 1.0.RC3
 */
public class AccessTokenFilter extends ControllerFilter {

    @Override
    protected void onInit() throws ServletException {

        Config config = getConfig();
        AccessTokenAuthenticationRequestFactory accessTokenAuthenticationRequestFactory =
            config.getAccessTokenAuthenticationRequestFactory();
        RefreshTokenAuthenticationRequestFactory refreshTokenAuthenticationRequestFactory =
                config.getRefreshTokenAuthenticationRequestFactory();
        RequestAuthorizer requestAuthorizer = config.getRequestAuthorizer();
        AccessTokenResultFactory accessTokenResultFactory = config.getAccessTokenResultFactory();
        RefreshTokenResultFactory refreshTokenResultFactory = config.getRefreshTokenResultFactory();
        Saver<AuthenticationResult> accountSaver = config.getAuthenticationResultSaver();
        Publisher<RequestEvent> eventPublisher = config.getRequestEventPublisher();
        BasicAuthenticationScheme basicAuthenticationScheme = config.getBasicAuthenticationScheme();

        AccessTokenController c = new AccessTokenController();
        c.setEventPublisher(eventPublisher);
        c.setAccessTokenAuthenticationRequestFactory(accessTokenAuthenticationRequestFactory);
        c.setAccessTokenResultFactory(accessTokenResultFactory);
        c.setRefreshTokenAuthenticationRequestFactory(refreshTokenAuthenticationRequestFactory);
        c.setRefreshTokenResultFactory(refreshTokenResultFactory);
        c.setAccountSaver(accountSaver);
        c.setRequestAuthorizer(requestAuthorizer);
        c.setBasicAuthenticationScheme(basicAuthenticationScheme);
        c.init();

        setController(c);

        super.onInit();
    }
}
