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
import com.stormpath.sdk.servlet.filter.DefaultLoginPageRedirector;
import com.stormpath.sdk.servlet.filter.MeFilter;
import com.stormpath.sdk.servlet.mvc.DefaultExpandsResolver;
import com.stormpath.sdk.servlet.mvc.MeController;

import javax.servlet.ServletContext;

/**
 * @since 1.0.0
 */
public class MeFilterFactory extends FilterFactory<MeFilter> {

    @Override
    protected MeFilter createInstance(ServletContext servletContext, Config config) throws Exception {

        MeController c = new MeController();
        c.setUri(config.getMeUrl());
        c.setProduces(config.getProducedMediaTypes());
        c.setExpandsResolver(new DefaultExpandsResolver(config.getMeExpandedProperties()));
        c.setObjectMapper(config.getObjectMapper());
        c.setLoginPageRedirector(new DefaultLoginPageRedirector(config.getLoginConfig().getUri()));
        c.setApplicationResolver(config.getApplicationResolver());
        c.init();

        MeFilter filter = new MeFilter();
        filter.setProducedMediaTypes(config.getProducedMediaTypes());
        filter.setController(c);

        return filter;
    }
}
