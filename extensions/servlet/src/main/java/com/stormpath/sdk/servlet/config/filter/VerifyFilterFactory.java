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
import com.stormpath.sdk.servlet.mvc.VerifyController;

/**
 * @since 1.0.0
 */
public class VerifyFilterFactory extends FormControllerFilterFactory<VerifyController> {

    @Override
    protected VerifyController newController() {
        return new VerifyController();
    }

    @Override
    protected ControllerConfig getResolver(Config config) {
        return config.getVerifyConfig();
    }

    @Override
    protected void doConfigure(VerifyController controller, Config config) {
        controller.setLoginUri(config.getLoginConfig().getUri());
        controller.setLoginNextUri(config.getLoginConfig().getNextUri());
        controller.setClient(config.getClient());
        controller.setAutoLogin(config.isRegisterAutoLoginEnabled());
        controller.setAuthenticationResultSaver(config.getAuthenticationResultSaver());
        controller.setAccountStoreResolver(config.getAccountStoreResolver());
    }
}
