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
import com.stormpath.sdk.servlet.mvc.CallbackController;
import com.stormpath.sdk.servlet.mvc.LogoutController;

/**
 * @since 1.0.0
 */
public abstract class CallbackControllerFilterFactory<T extends CallbackController> extends ControllerFilterFactory<T> {

    @Override
    protected void configure(T controller, Config config) throws Exception {
        controller.setEventPublisher(config.getRequestEventPublisher());

        controller.setLoginNextUri(getConfig().getLoginConfig().getNextUri());
        controller.setAuthenticationResultSaver(getConfig().getAuthenticationResultSaver());
        controller.setEventPublisher(config.getRequestEventPublisher());
        controller.setProduces(config.getProducedMediaTypes());

        LogoutController logoutController = new LogoutController();
        logoutController.setNextUri(config.getLogoutConfig().getNextUri());
        logoutController.setInvalidateHttpSession(config.isLogoutInvalidateHttpSession());
        logoutController.setProduces(config.getProducedMediaTypes());
        logoutController.init();

        controller.setLogoutController(logoutController);

        //Let's give the chance to sub-classes of this factory to configure this controller as well
        doConfigure(controller, config);
    }

    public abstract void doConfigure(T controller, Config config);
}
