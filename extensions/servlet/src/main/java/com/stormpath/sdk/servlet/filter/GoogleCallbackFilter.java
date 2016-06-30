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
import com.stormpath.sdk.servlet.mvc.provider.GoogleCallbackController;

import javax.servlet.ServletException;

/**
 * @since 1.0.0
 */
public class GoogleCallbackFilter extends ControllerFilter {

    @Override
    protected void onInit() throws ServletException {
        GoogleCallbackController googleCallbackController = new GoogleCallbackController(
                getConfig().getLoginControllerConfig().getNextUri(),
                getConfig().getAuthenticationResultSaver(),
                getConfig().<Publisher<RequestEvent>>getInstance(EVENT_PUBLISHER)
        );

        setController(googleCallbackController);

        super.onInit();
    }
}
