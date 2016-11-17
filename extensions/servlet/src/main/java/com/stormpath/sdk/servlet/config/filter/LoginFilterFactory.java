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
package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.ControllerConfig;
import com.stormpath.sdk.servlet.mvc.LoginController;

/**
 * @since 1.0.0
 */
public class LoginFilterFactory extends FormControllerFilterFactory<LoginController> {

    @Override
    protected LoginController newController() {
        return new LoginController();
    }

    @Override
    protected ControllerConfig getResolver(Config config) {
        return config.getLoginConfig();
    }

    @Override
    protected void doConfigure(LoginController controller, Config config) {
        controller.setForgotPasswordEnabled(config.getForgotPasswordConfig().isEnabled());
        controller.setForgotPasswordUri(config.getForgotPasswordConfig().getUri());
        controller.setVerifyEnabled(config.getVerifyConfig().isEnabled());
        controller.setVerifyUri(config.getVerifyConfig().getUri());
        controller.setRegisterEnabledResolver(config.getRegisterEnabledResolver());
        controller.setRegisterUri(config.getRegisterConfig().getUri());
        controller.setLogoutUri(config.getLogoutConfig().getUri());
        controller.setAuthenticationResultSaver(config.getAuthenticationResultSaver());
        controller.setPreLoginHandler(config.getLoginPreHandler());
        controller.setPostLoginHandler(config.getLoginPostHandler());
        controller.setIdSiteEnabled(config.isIdSiteEnabled());
        controller.setCallbackEnabled(config.isCallbackEnabled());
        controller.setApplicationResolver(config.getApplicationResolver());
    }
}
