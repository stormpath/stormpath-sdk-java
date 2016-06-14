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
 * TODO this should be a single configuration and a single controller according to the spec but to I'm keeping as it is for now.
 *
 * @since 1.0.0
 */
public class SendVerificationEmailControllerConfigResolver extends AbstractSpringControllerConfigResolver {

    @Value("#{ @environment['stormpath.web.verifyEmail.enabled'] ?: true }")
    protected boolean verifyEnabled;

    @Value("#{ @environment['stormpath.web.verifyEmail.view'] ?: 'stormpath/verify' }")
    protected String verifyView;

    @Value("#{ @environment['stormpath.web.sendVerificationEmail.uri'] ?: '/sendVerificationEmail' }")
    protected String sendVerificationEmailUri;

    @Value("#{ @environment['stormpath.web.sendVerificationEmail.view'] ?: 'stormpath/sendVerificationEmail' }")
    protected String sendVerificationEmailView;

    @Override
    public String getView() {
        return sendVerificationEmailView;
    }

    @Override
    public String getUri() {
        return sendVerificationEmailUri;
    }

    @Override
    public String getNextUri() {
        return verifyView;
    }

    @Override
    public boolean isEnabled() {
        return verifyEnabled;
    }

    @Override
    public String getControllerKey() {
        return "sendVerificationEmail";
    }

    @Override
    protected String[] getDefaultFieldOrder() {
        return new String[0];
    }
}
