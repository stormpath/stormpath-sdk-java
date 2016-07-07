/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.Providers;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.mvc.AbstractSocialCallbackController;
import com.stormpath.sdk.servlet.util.ServletUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.0
 */
public class LinkedinCallbackController extends AbstractSocialCallbackController {

    public LinkedinCallbackController(String loginNextUri, Saver<AuthenticationResult> authenticationResultSaver, Publisher<RequestEvent> eventPublisher) {
        super(loginNextUri, authenticationResultSaver, eventPublisher);
    }

    @Override
    protected ProviderAccountRequest getAccountProviderRequest(HttpServletRequest request) {
        String code = ServletUtils.getCleanParam(request, "code");
        return Providers.LINKEDIN.account().setCode(code).build();
    }
}
