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

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.VerifyController;

import javax.servlet.ServletException;
import java.util.Locale;

/**
 * @since 1.0.RC3
 */
public class VerifyFilter extends ControllerFilter {

    public static final String EVENT_PUBLISHER = "stormpath.web.request.event.publisher";
    public static final String ACCOUNT_STORE_RESOLVER = "stormpath.web.accountStoreResolver";
    public static final String ACCOUNT_SAVER_PROP = "stormpath.web.authc.saver";
    public static final String MESSAGE_SOURCE = "stormpath.web.message.source";
    public static final String LOCALE_RESOLVER = "stormpath.web.locale.resolver";

    @Override
    protected void onInit() throws ServletException {

        Client client = getClient();
        Publisher<RequestEvent> eventPublisher = getConfig().getInstance(EVENT_PUBLISHER);
        Saver<AuthenticationResult> authenticationResultSaver = getConfig().getInstance(ACCOUNT_SAVER_PROP);
        AccountStoreResolver accountStoreResolver = getConfig().getInstance(ACCOUNT_STORE_RESOLVER);
        MessageSource messageSource = getConfig().getInstance(MESSAGE_SOURCE);
        Resolver<Locale> localeResolver = getConfig().getInstance(LOCALE_RESOLVER);

        VerifyController controller = new VerifyController();
        controller.setUri(getConfig().getVerifyUrl());
        controller.setNextUri(getConfig().getVerifyNextUrl());
        controller.setLogoutUri(getConfig().getLogoutUrl());
        controller.setLoginUri(getConfig().getLoginUrl());
        controller.setAccountStoreResolver(accountStoreResolver);
        controller.setView("stormpath/verify");
        controller.setAutoLogin(getConfig().getRegisterAutoLogin());
        controller.setClient(client);
        controller.setEventPublisher(eventPublisher);
        controller.setAuthenticationResultSaver(authenticationResultSaver);
        controller.setMessageSource(messageSource);
        controller.setLocaleResolver(localeResolver);
        controller.init();

        setController(controller);

        super.onInit();
    }
}
