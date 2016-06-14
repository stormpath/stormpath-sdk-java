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
public class LogoutControllerConfigResolver extends AbstractSpringControllerConfigResolver {

    @Value("#{ @environment['stormpath.web.logout.enabled'] ?: true }")
    protected boolean logoutEnabled;

    @Value("#{ @environment['stormpath.web.logout.uri'] ?: '/logout' }")
    protected String logoutUri;

    @Value("#{ @environment['stormpath.web.logout.nextUri'] ?: '/' }")
    protected String logoutNextUri;

    @Override
    public String getView() {
        return null;
    }

    @Override
    public String getUri() {
        return logoutUri;
    }

    @Override
    public String getNextUri() {
        return logoutNextUri;
    }

    @Override
    public boolean isEnabled() {
        return logoutEnabled;
    }

    @Override
    public String getControllerKey() {
        return "logout";
    }

    @Override
    protected String[] getDefaultFieldOrder() {
        return new String[0];
    }
}
