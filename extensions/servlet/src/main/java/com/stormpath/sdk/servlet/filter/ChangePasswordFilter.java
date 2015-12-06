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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.ChangePasswordController;

import javax.servlet.ServletException;
import java.util.Locale;

/**
 * @since 1.0.RC3
 */
public class ChangePasswordFilter extends ControllerFilter {

    public static final String CSRF_TOKEN_MANAGER = "stormpath.web.csrf.token.manager";
    public static final String MESSAGE_SOURCE = "stormpath.web.message.source";
    public static final String LOCALE_RESOLVER = "stormpath.web.locale.resolver";

    @Override
    protected void onInit() throws ServletException {

        CsrfTokenManager csrfTokenManager = getConfig().getInstance(CSRF_TOKEN_MANAGER);
        Resolver<Locale> localeResolver = getConfig().getInstance(LOCALE_RESOLVER);
        MessageSource messageSource = getConfig().getInstance(MESSAGE_SOURCE);

        ChangePasswordController controller = new ChangePasswordController();
        controller.setUri(getConfig().getChangePasswordUrl());
        controller.setView("stormpath/change");
        controller.setCsrfTokenManager(csrfTokenManager);
        controller.setNextUri(getConfig().getChangePasswordNextUrl());
        controller.setLoginUri(getConfig().getLoginUrl());
        controller.setForgotPasswordUri(getConfig().getForgotPasswordUrl());
        controller.setLocaleResolver(localeResolver);
        controller.setMessageSource(messageSource);
        controller.init();

        setController(controller);

        super.onInit();
    }

}


