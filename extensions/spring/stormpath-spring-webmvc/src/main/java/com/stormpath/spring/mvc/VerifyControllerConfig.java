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
public class VerifyControllerConfig extends AbstractSpringControllerConfig {

    @Value("#{ @environment['stormpath.web.verifyEmail.enabled'] ?: true }")
    protected boolean verifyEnabled;

    @Value("#{ @environment['stormpath.web.verifyEmail.uri'] ?: '/verify' }")
    protected String verifyUri;

    @Value("#{ @environment['stormpath.web.verifyEmail.nextUri'] ?: '/login?status=verified' }")
    protected String verifyNextUri;

    @Value("#{ @environment['stormpath.web.verifyEmail.view'] ?: 'stormpath/verify' }")
    protected String verifyView;

    @Override
    public String getView() {
        return verifyView;
    }

    @Override
    public String getUri() {
        return verifyUri;
    }

    @Override
    public String getNextUri() {
        return verifyNextUri;
    }

    @Override
    public boolean isEnabled() {
        return verifyEnabled;
    }

    @Override
    public String getControllerKey() {
        return "verifyEmail";
    }

    @Override
    protected String[] getDefaultFieldOrder() {
        return new String[0];
    }
}
