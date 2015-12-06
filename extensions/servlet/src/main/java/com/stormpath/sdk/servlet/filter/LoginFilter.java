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
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.mvc.LoginController;

import javax.servlet.ServletException;

/**
 * @since 1.0.RC3
 */
public class LoginFilter extends ControllerFilter {

    public static final String AUTHENTICATION_RESULT_SAVER = "stormpath.web.authc.saver";
    public static final String CSRF_TOKEN_MANAGER = "stormpath.web.csrf.token.manager";

    @Override
    protected void onInit() throws ServletException {

        Saver<AuthenticationResult> authenticationResultSaver = getConfig().getInstance(AUTHENTICATION_RESULT_SAVER);
        CsrfTokenManager csrfTokenManager = getConfig().getInstance(CSRF_TOKEN_MANAGER);

        LoginController controller = new LoginController();
        controller.setUri(getConfig().getLoginUrl());
        controller.setView("stormpath/login");
        controller.setNextUri(getConfig().getLoginNextUrl());
        controller.setForgotLoginUri(getConfig().getForgotPasswordUrl());
        controller.setRegisterUri(getConfig().getRegisterUrl());
        controller.setLogoutUri(getConfig().getLogoutUrl());
        controller.setAuthenticationResultSaver(authenticationResultSaver);
        controller.setCsrfTokenManager(csrfTokenManager);
        controller.init();

        setController(controller);

        super.onInit();
    }

}
