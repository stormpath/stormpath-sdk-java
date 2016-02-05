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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.RequestEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.0.RC3
 */
public class RequestEventPublisher implements Publisher<RequestEvent> {

    private final List<RequestEventListener> listeners;

    public RequestEventPublisher(RequestEventListener listener) {
        Assert.notNull(listener, "listener argument cannot be null.");
        this.listeners = new ArrayList<RequestEventListener>();
        this.listeners.add(listener);
    }

    public RequestEventPublisher(List<RequestEventListener> listeners) {
        Assert.notNull(listeners, "listeners argument cannot be null.");
        this.listeners = listeners;
    }

    @Override
    public void publish(RequestEvent e) {

        Assert.notNull(e, "RequestEvent argument cannot be null.");

        //visitor pattern / double dispatch for type safe event handling:
        for (RequestEventListener listener : listeners) {
            e.accept(listener);
        }
    }
}
