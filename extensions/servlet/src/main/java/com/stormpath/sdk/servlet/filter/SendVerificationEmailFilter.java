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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.mvc.SendVerificationEmailController;

import javax.servlet.ServletException;

/**
 * @since 1.0.RC8.3
 */
public class SendVerificationEmailFilter extends ControllerFilter {

    public static final String CSRF_TOKEN_MANAGER = "stormpath.web.csrf.token.manager";
    public static final String ACCOUNT_STORE_RESOLVER = "stormpath.web.accountStoreResolver";

    @Override
    protected void onInit() throws ServletException {
        CsrfTokenManager csrfTokenManager = getConfig().getInstance(CSRF_TOKEN_MANAGER);
        AccountStoreResolver accountStoreResolver = getConfig().getInstance(ACCOUNT_STORE_RESOLVER);

        SendVerificationEmailController controller = new SendVerificationEmailController();
        controller.setView("stormpath/sendVerificationEmail");
        controller.setCsrfTokenManager(csrfTokenManager);
        controller.setAccountStoreResolver(accountStoreResolver);
        controller.setNextUri("stormpath/verify");
        controller.setLoginUri(getConfig().getLoginUrl());
        controller.init();

        setController(controller);

        super.onInit();
    }
}
