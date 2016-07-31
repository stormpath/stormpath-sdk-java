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
    protected void configure(T c, Config config) throws Exception {
        c.setEventPublisher(config.getRequestEventPublisher());

        c.setLoginNextUri(getConfig().getLoginConfig().getNextUri());
        c.setAuthenticationResultSaver(getConfig().getAuthenticationResultSaver());
        c.setEventPublisher(config.getRequestEventPublisher());
        c.setProduces(config.getProducedMediaTypes());

        LogoutController controller = new LogoutController();
        controller.setNextUri(config.getLogoutConfig().getNextUri());
        controller.setInvalidateHttpSession(config.isLogoutInvalidateHttpSession());
        controller.setProduces(config.getProducedMediaTypes());
        controller.init();

        c.setLogoutController(controller);

        //Let's give the chance to sub-classes of this factory to configure this controller as well
        doConfigure(c, config);
    }

    public abstract void doConfigure(T c, Config config);
}
