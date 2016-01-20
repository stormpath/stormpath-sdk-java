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
package com.stormpath.spring.event;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.event.impl.EventPublisherFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;

/**
 * @since 1.0.RC8.1
 */
//TODO TM: Check if this is useful for servlet plugin
@Service
public class EventPublisherFactoryFactory extends ConfigSingletonFactory<EventPublisherFactory> {
//public class EventPublisherFactoryFactory extends AbstractFactoryBean<EventPublisherFactory> {

    public static final String REQUEST_EVENT_PUBLISHER = "stormpath.web.request.event.publisher";

    public EventPublisherFactoryFactory() {
        System.out.println();
    }

    @Override
    protected EventPublisherFactory createInstance(ServletContext sc) throws Exception {
        EventPublisherFactory eventPublisherFactory = getConfig().getInstance(REQUEST_EVENT_PUBLISHER);
        return eventPublisherFactory;
    }
}
