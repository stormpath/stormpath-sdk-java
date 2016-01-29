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
package com.stormpath.sdk.servlet.event.impl;

import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.event.RequestEventListener;
import com.stormpath.sdk.servlet.event.TokenRevocationRequestEventListener;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.0.RC3
 */
public class EventPublisherFactory extends ConfigSingletonFactory<Publisher> {

    public static final String REQUEST_EVENT_LISTENER = "stormpath.web.request.event.listener";

    public static final RequestEventListener tokenRevocation = new TokenRevocationRequestEventListener();  //revoke access and refresh tokens after logout

    @Override
    protected Publisher createInstance(ServletContext servletContext) throws Exception {
        RequestEventListener configuredListener = getConfig().getInstance(REQUEST_EVENT_LISTENER);
        List<RequestEventListener> listeners = new ArrayList<RequestEventListener>();
        listeners.add(configuredListener);
        listeners.add(tokenRevocation);
        return new RequestEventPublisher(listeners);
    }
}
