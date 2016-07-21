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

import java.util.Arrays;
import java.util.List;

/**
 * @since 1.0.0
 */
public class RegisterControllerConfigResolver extends AbstractSpringControllerConfigResolver {

    private static final String[] DEFAULT_FIELD_NAMES = new String[]{"username", "givenName", "middleName", "surname", "email", "password", "confirmPassword"};

    @Value("#{ @environment['stormpath.web.register.enabled'] ?: true }")
    protected boolean registerEnabled;

    @Value("#{ @environment['stormpath.web.register.uri'] ?: '/register' }")
    protected String registerUri;

    @Value("#{ @environment['stormpath.web.register.nextUri'] ?: '/' }")
    protected String registerNextUri;

    @Value("#{ @environment['stormpath.web.register.view'] ?: 'register' }")
    protected String registerView;

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

    @Override
    public String getControllerKey() {
        return "register";
    }

    @Override
    protected String[] getDefaultFieldOrder() {
        return DEFAULT_FIELD_NAMES;
    }

    @Override
    protected List<String> getDefaultDisableFields() {
        return Arrays.asList("username", "middleName", "confirmPassword");
    }
}
