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
public class RegisterControllerConfig extends AbstractSpringControllerConfig {

    @Value("#{ @environment['stormpath.web.register.enabled'] ?: true }")
    protected boolean registerEnabled;

    @Value("#{ @environment['stormpath.web.register.uri'] ?: '/register' }")
    protected String registerUri;

    @Value("#{ @environment['stormpath.web.register.nextUri'] ?: '/' }")
    protected String registerNextUri;

    @Value("#{ @environment['stormpath.web.register.view'] ?: 'stormpath/register' }")
    protected String registerView;

    public RegisterControllerConfig() {
        super("register");
        setDefaultFieldNames("username", "givenName", "middleName", "surname", "email", "password", "confirmPassword");
        setDisabledFieldNames("username", "middleName", "confirmPassword");
    }

    @Override
    public String getView() {
        return registerView;
    }

    @Override
    public String getUri() {
        return registerUri;
    }

    @Override
    public String getNextUri() {
        return registerNextUri;
    }

    @Override
    public boolean isEnabled() {
        return registerEnabled;
    }
}
