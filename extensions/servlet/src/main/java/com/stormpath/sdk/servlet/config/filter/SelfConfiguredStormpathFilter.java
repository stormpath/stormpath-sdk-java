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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.filter.FilterChainResolver;
import com.stormpath.sdk.servlet.filter.StormpathFilter;
import com.stormpath.sdk.servlet.filter.WrappedServletRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * A {@link StormpathFilter} that self-configures itself by looking up {@link Client}, {@link Application}, and
 * {@link Config} instances from the {@link javax.servlet.ServletContext}.  Default implementation when Dependency
 * Injection is not available.
 *
 * @since 1.0.0
 */
public class SelfConfiguredStormpathFilter extends StormpathFilter {

    private static final Logger log = LoggerFactory.getLogger(SelfConfiguredStormpathFilter.class);

    @Override
    protected void onInit() throws ServletException {
        try {
            doInit();
            if (isEnabled()) {
                super.onInit();
            }
        } catch (ServletException e) {
            log.error("Unable to initialize StormpathFilter.", e);
            throw e;
        } catch (Exception e) {
            String msg = "Unable to initialize StormpathFilter: " + e.getMessage();
            log.error(msg, e);
            throw new ServletException(msg);
        }
    }

    protected void doInit() throws ServletException {
        ServletContext servletContext = getServletContext();
        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);
        setEnabled(config.isStormpathWebEnabled());

        if (isEnabled()) {
            setClient(config.getClient());
            setApplication(config.getApplicationResolver().getApplication(servletContext));

            FilterChainResolver resolver = config.getInstance("stormpath.web.filter.chain.resolver");
            setFilterChainResolver(resolver);

            String val = config.get("stormpath.web.request.client.attributeNames");
            if (Strings.hasText(val)) {
                String[] vals = Strings.split(val);
                setClientRequestAttributeNames(new LinkedHashSet<>(Arrays.asList(vals)));
            }

            val = config.get("stormpath.web.request.application.attributeNames");
            if (Strings.hasText(val)) {
                String[] vals = Strings.split(val);
                setApplicationRequestAttributeNames(new LinkedHashSet<>(Arrays.asList(vals)));
            }

            WrappedServletRequestFactory factory = config.getInstance("stormpath.web.request.factory");
            setWrappedServletRequestFactory(factory);
        }
    }
}
