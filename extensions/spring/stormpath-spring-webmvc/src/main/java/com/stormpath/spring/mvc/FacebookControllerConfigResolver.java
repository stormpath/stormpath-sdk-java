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
package com.stormpath.spring.mvc;

import org.springframework.beans.factory.annotation.Value;

/**
 * @since 1.0.0
 */
public class FacebookControllerConfigResolver extends AbstractSpringControllerConfigResolver {

    @Value("#{ @environment['stormpath.web.social.facebook.enabled'] ?: true }")
    protected boolean enabled;

    @Value("#{ @environment['stormpath.web.social.facebook.uri'] ?: '/callbacks/facebook' }")
    protected String uri;

    @Value("#{ @environment['stormpath.web.social.facebook.scope'] ?: 'email' }")
    protected String scope;

    public FacebookControllerConfigResolver() {
        System.out.print("aa");
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getControllerKey() {
        return "social.facebook";
    }

    @Override
    public String getNextUri() {
        return null; //Not relevant;
    }

    @Override
    public String getView() {
        return null; //Not relevant
    }

    @Override
    protected String[] getDefaultFieldOrder() {
        return new String[0];
    }
}
