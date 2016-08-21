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
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.mvc.AbstractController;
import com.stormpath.sdk.servlet.mvc.DefaultProviderAccountRequestFactory;

import javax.servlet.ServletContext;

/**
 * @since 1.0.0
 */
public abstract class ControllerFilterFactory<T extends AbstractController> extends FilterFactory<ControllerFilter> {

    @Override
    protected final ControllerFilter createInstance(ServletContext servletContext, Config config) throws Exception {

        T c = newController();
        c.setAccountResolver(config.getAccountResolver());
        c.setContentNegotiationResolver(config.getContentNegotiationResolver());
        c.setEventPublisher(config.getRequestEventPublisher());
        c.setLocaleResolver(config.getLocaleResolver());
        c.setMessageSource(config.getMessageSource());
        c.setProduces(config.getProducedMediaTypes());
        c.setApplicationResolver(config.getApplicationResolver());
        // Refactor of Provider requests for
        // https://github.com/stormpath/stormpath-sdk-java/issues/915
        // and to provide uniform responses across all integrations for
        // conformance to stormpath-framework-spec as enforced by
        // stormpath-framework-tck
        /**
         * @since 1.0.3
         */
        c.setProviderAccountRequestFactory(new DefaultProviderAccountRequestFactory());

        configure(c, config);

        c.init();

        ControllerFilter filter = new ControllerFilter();
        filter.setProducedMediaTypes(config.getProducedMediaTypes());
        filter.setController(c);

        return filter;
    }

    protected abstract T newController();

    protected abstract void configure(T c, Config config) throws Exception;
}
