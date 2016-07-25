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

import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.mvc.IdSiteResultController;
import com.stormpath.sdk.servlet.mvc.LogoutController;
import com.stormpath.sdk.servlet.mvc.SamlResultController;

import javax.servlet.ServletException;

/**
 * @since 1.0.0
 */
public class IDSiteResultFilter extends ControllerFilter {

    @Override
    protected void onInit() throws ServletException {
        Publisher<RequestEvent> eventPublisher = getConfig().getRequestEventPublisher();

        LogoutController logoutController = new LogoutController(getConfig().getLogoutControllerConfig(), getConfig().getProducesMediaTypes());
        logoutController.setLogoutInvalidateHttpSession(getConfig().isLogoutInvalidateHttpSession())
                .setLogoutNextUri(getConfig().getLogoutControllerConfig().getNextUri());

        IdSiteResultController controller = new IdSiteResultController();
        controller.setLoginNextUri(getConfig().getLoginControllerConfig().getNextUri());
        controller.setLogoutController(logoutController);
        controller.setAuthenticationResultSaver(getConfig().getAuthenticationResultSaver());
        controller.setEventPublisher(eventPublisher);
        controller.setAccessTokenResultFactory(getConfig().getAccessTokenResultFactory());
        controller.setRegisterNextUri(getConfig().getRegisterControllerConfig().getNextUri());

        //TODO 542: listener here. See stormpathIdSiteResultController#stormpathIdSiteResultController
//        if (springSecurityIdSiteResultListener != null) {
//            controller.addIdSiteResultListener(springSecurityIdSiteResultListener);
//        }

        setController(controller);

        super.onInit();
    }
}
