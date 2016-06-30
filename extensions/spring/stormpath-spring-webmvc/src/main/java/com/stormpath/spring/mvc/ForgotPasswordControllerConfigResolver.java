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
public class ForgotPasswordControllerConfigResolver extends AbstractSpringControllerConfigResolver {

    @Value("#{ @environment['stormpath.web.forgotPassword.enabled'] ?: true }")
    protected boolean forgotEnabled;

    @Value("#{ @environment['stormpath.web.forgotPassword.uri'] ?: '/forgot' }")
    protected String forgotUri;

    @Value("#{ @environment['stormpath.web.forgotPassword.nextUri'] ?: '/login?status=forgot' }")
    protected String forgotNextUri;

    @Value("#{ @environment['stormpath.web.forgotPassword.view'] ?: 'stormpath/forgot-password' }")
    protected String forgotView;

    @Override
    public String getView() {
        return forgotView;
    }

    @Override
    public String getUri() {
        return forgotUri;
    }

    @Override
    public String getNextUri() {
        return forgotNextUri;
    }

    @Override
    public boolean isEnabled() {
        return forgotEnabled;
    }

    @Override
    public String getControllerKey() {
        return "forgotPassword";
    }

    @Override
    protected String[] getDefaultFieldOrder() {
        return new String[0];
    }
}
