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
package com.stormpath.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

@ConfigurationProperties(prefix = "stormpath.web.stormpathFilter")
public class StormpathFilterProperties {

    private boolean enabled = true;
    private int order = Ordered.HIGHEST_PRECEDENCE;
    private Collection<String> urlPatterns = Arrays.asList("/*");
    private Collection<String> servletNames = Collections.emptySet();
    private EnumSet<DispatcherType> dispatcherTypes =
        EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE, DispatcherType.FORWARD, DispatcherType.ERROR);
    private boolean matchAfter = false;

    public StormpathFilterProperties() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Collection<String> getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(Collection<String> urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public Collection<String> getServletNames() {
        return servletNames;
    }

    public void setServletNames(Collection<String> servletNames) {
        this.servletNames = servletNames;
    }

    public EnumSet<DispatcherType> getDispatcherTypes() {
        return dispatcherTypes;
    }

    public void setDispatcherTypes(EnumSet<DispatcherType> dispatcherTypes) {
        this.dispatcherTypes = dispatcherTypes;
    }

    public boolean isMatchAfter() {
        return matchAfter;
    }

    public void setMatchAfter(boolean matchAfter) {
        this.matchAfter = matchAfter;
    }
}
